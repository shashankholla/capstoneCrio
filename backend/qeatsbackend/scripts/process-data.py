# process-data.py
# NOTE
# It looks like the json files have data pulled directly from a previously existing zomato api.
# So, it has more data such as links to images, menus, etc.
# The csv file however, has more data than the json files.
# So intersecting both seems to be a decent solution.
# NOTE
# Zomato dataset has around 5.5k rows which makes querying, developing easy
# Use `is_production` flag to increase that by 100x in production env

import ast
import csv
import json
import math
import os
import random
import string
import sys
import numpy
from collections import OrderedDict
from pymongo import MongoClient

json_files = ["file2.json", "file3.json"]
csv_file = "zomato.csv"
menus_csv_file = (
    "zomato-bangalore-restaurants.csv"
)  # it has more than food, but take only that

# change this flag is using more data, etc
is_production = False
MULTIPLIER = 50


def getOpensAt():
    """
    Select an opening time for the restaurant from a list of opening times.
    """
    opening_times = ["08:30", "09:00", "09:30", "10:00", "11:00"]
    return opening_times[int(round(random.random() * len(opening_times) - 1))]


def getClosesAt():
    """
    Select an closing time for the restaurant from a list of closing times.
    """
    closing_times = ["22:00", "22:15", "22:30", "23:00", "23:30"]
    return closing_times[int(round(random.random() * len(closing_times) - 1))]


def add_datapoints_then_insert(restaurants):
    """
    Is called if more data is needed.
    Should be called only if production flag is True.
    """
    current_len = len(restaurants)
    new_len = (MULTIPLIER - 1) * current_len  # because we add to existing restaurants

    # these are NOT chosen scientifically
    restaurant_latitudes = numpy.linspace(77.1, 77.5, 1000)
    restaurant_longitudes = numpy.linspace(28.3, 28.7, 1000)
    restaurant_cities = ["Bangalore", "Delhi", "Mumbai", "Kolkata", "Chennai"]
    restaurant_endings = [
        " Cafe",
        " Bistro",
        " Kitchen",
        " Foods",
        " Table",
        " Grub",
        "'s Takeaway" "'s Place",
    ]
    new_restaurant_names = []

    # NOTE
    # This will only work on unix systems
    # The words file has over 200k words
    word_file = "/usr/share/dict/words"
    try:
        words = open(word_file).read().splitlines()
        words_len = len(words)
        endings_len = len(restaurant_endings)
        for i in range(current_len):
            r = math.floor(random.random() * words_len)
            ei = math.floor(random.random() * endings_len)
            words[r] = words[r].title()
            new_restaurant_names.append(words[r] + restaurant_endings[ei])
    except:
        print(sys.exc_info()[0], sys.exc_info()[1])
        sys.exit("No words file found. Exiting.")

    num_latitudes = len(restaurant_latitudes)
    num_longitudes = len(restaurant_longitudes)
    num_cities = len(restaurant_cities)
    first_batch = restaurants

    for batch_index in range(MULTIPLIER):
        print("Inserting batch: {0}/{1}".format(batch_index + 1, MULTIPLIER))

        if batch_index == 0:
            insert_to_collection(restaurants, "restaurants")
        else:
            restaurants = []
            for i in range(current_len):
                resto_ri = math.floor(random.random() * current_len)
                lat_ri = math.floor(random.random() * num_latitudes)
                long_ri = math.floor(random.random() * num_longitudes)
                city_ri = math.floor(random.random() * num_cities)

                random_id = "".join(
                    random.choices(string.ascii_uppercase + string.digits, k=10)
                )
                random_name = new_restaurant_names[i]
                image_url = first_batch[resto_ri]["imageUrl"]

                restaurant = Restaurant(random_id, random_name, image_url)
                restaurant.city = restaurant_cities[city_ri]
                restaurant.latitude = restaurant_latitudes[lat_ri]
                restaurant.longitude = restaurant_longitudes[long_ri]
                restaurant.attributes = first_batch[resto_ri]["attributes"]

                restaurants.append(restaurant.asdict())

            insert_to_collection(restaurants, "restaurants")


def process():
    """
    Process both json files and csv file.
    """
    cwd = os.getcwd()
    fjson_list = []
    json_restaurants_dict = {}

    # parse the json files and store their references
    for jf in json_files:
        path = os.path.join(cwd, jf)
        with open(path) as fp:
            fjson_restaurants = json.load(fp)
            for row in fjson_restaurants:
                if "restaurants" in row:
                    for resto in row["restaurants"]:
                        json_restaurants_dict[resto["restaurant"]["id"]] = resto[
                            "restaurant"
                        ]

    # parse the csv file
    path = os.path.join(cwd, csv_file)
    indian_restaurant_rows = []
    with open(path, encoding="latin1") as fp:
        datareader = csv.reader(fp, delimiter=",")
        for row in datareader:
            if row[2] == "1":
                indian_restaurant_rows.append(row)

    # get restaurants only with image url
    count = 0
    restaurants = []
    for row in indian_restaurant_rows:
        restaurant_id = row[0]
        if restaurant_id in json_restaurants_dict:
            resto = json_restaurants_dict[restaurant_id]
            if "featured_image" in resto and resto["featured_image"] != "":
                restaurant = Restaurant(restaurant_id, row[1], resto["featured_image"])
                restaurant.city = row[3]
                restaurant.latitude = float(row[7])
                restaurant.longitude = float(row[8])
                restaurant.attributes = str(row[9]).split(",")
                restaurants.append(restaurant.asdict())

    if is_production:
        add_datapoints_then_insert(restaurants)
    else:
        # insert to database
        insert_to_collection(restaurants, "restaurants")

    # parse bangalore zomato csv file
    # NOTE there are some problems after row 1120 and beyond so just taking the shortcut
    path = os.path.join(cwd, menus_csv_file)
    food_item_dict = OrderedDict()
    with open(path) as fp:
        datareader = csv.reader(fp)
        next(datareader)
        for index in range(500):
            row = next(datareader)
            if row[14] != None or row[14] != "":
                try:
                    food_items = ast.literal_eval(row[14])
                    if len(food_items) != 0:
                        food_item_dict.update(OrderedDict.fromkeys(food_items))
                except:
                    continue

    insert_food_and_menus_to_db(food_item_dict)


def insert_food_and_menus_to_db(food_item_dict: OrderedDict):
    """
    Get restaurants from the database and have them associated with menus and food items.
    """
    client = MongoClient()
    db = client["restaurant-database"]

    restaurants_collection = db["restaurants"]
    foods = list(food_item_dict.keys())
    num_foods = len(foods)

    # Foods
    print("Inserting all foods..")

    food_fixed_attributes = [
        "Tasty",
        "Spicy",
        "Sweet",
        "Savory",
        "Salty",
        "Gravy",
        "Popular",
    ]
    food_items = []
    for i in range(len(foods)):
        food_id = "".join(random.choices(string.ascii_uppercase + string.digits, k=10))
        food_price = math.floor(random.random() * 500) + 200
        food_attributes = [
            random.choice(food_fixed_attributes),
            random.choice(food_fixed_attributes),
        ]
        food_item = Item(food_id, foods[i], "", food_price, food_attributes)

        food_items.append(food_item.asdict())

    # insert into database
    insert_to_collection(food_items, "items")

    # Menus
    print("Inserting menus..")

    menus = []
    for restaurant in restaurants_collection.find():
        start_index = round(random.random() * (num_foods / 2))
        selected_num_foods = round(random.random() * 100)
        cursor = db["items"].find(
            projection={"_id": False}, skip=start_index, limit=selected_num_foods
        )

        selected_foods = []
        for doc in cursor:
            selected_foods.append(doc)

        menu = Menu(restaurant["id"], selected_foods)
        menus.append(menu.asdict())

    insert_to_collection(menus, "menus")


def insert_to_collection(documents, collection_name):
    """
    Insert the restaurant data into the database
    """
    client = MongoClient()
    db = client["restaurant-database"]
    db[collection_name].insert_many(documents)
    client.close()


class Restaurant:
    """
    Restaurant class.
    """

    city = ""
    image_url = ""
    latitude = 0.0
    longitude = 0.0
    attributes = []
    opensAt = ""
    closesAt = ""

    def __init__(self, id, name, image_url):
        self.id = id
        self.name = name
        self.image_url = image_url
        self.opensAt = getOpensAt()
        self.closesAt = getClosesAt()

    def __str__(self):
        return "{0}, {1}, {2}, {3}".format(
            self.id, self.name, self.city, self.attributes
        )

    def asdict(self):
        return {
            "id": self.id,
            "name": self.name,
            "imageUrl": self.image_url,
            "latitude": self.latitude,
            "longitude": self.longitude,
            "attributes": self.attributes,
            "opensAt": self.opensAt,
            "closesAt": self.closesAt,
        }


class Menu:
    """
    Represents a menu for a restaurant.
    """

    restaurantId = ""
    items = []

    def __init__(self, restaurantId, items):
        self.restaurantId = restaurantId
        self.items = items

    def asdict(self):
        return {"restaurantId": self.restaurantId, "items": self.items}


class Item:
    """
    Food item class.
    """

    id = ""
    name = ""
    imageUrl = ""
    price = 0
    attributes = []

    def __init__(self, id, name, imageUrl, price, attributes):
        self.id = id
        self.name = name
        self.imageUrl = imageUrl
        self.price = price
        self.attributes = attributes

    def asdict(self):
        return {
            "id": self.id,
            "name": self.name,
            "imageUrl": self.imageUrl,
            "price": self.price,
            "attributes": self.attributes,
        }


if __name__ == "__main__":
    process()
