Login / Register
    -- Registration - users database that has
                            - username, resId, fullName
                            - Upon user creation add (resId, resName, resAddress, resAttributes, resLocation, resAttributes) to restaurant db
    -- Login - send username and password to server. respond with 
                    resId, resName, resAddress, menu, pendingOrders, orderHistory(previous 10), processingOrders
                    

Dashboard -> View Menu - receive only Menu for the given resId
             Edit Menu - send back menu with resId 
             Update Menu and Quanity - send back menu with resId
             View Pending Orders - get pendingOrders
             View Accepted but Unfinished orders - getProcessingOrders
             Order History - previous 10


