# TODO: CRIO_TASK_MODULE_DEPLOYMENT: Create the Dockerfile so that you can deploy your Spring Boot
# application easily.
FROM gradle:alpine
USER root
RUN apk update && \
    apk add --no-cache bash git
RUN echo "Cloning"

RUN git clone https://aws-instance-token:4W1EdzpmgrR61thobiyP@gitlab.crio.do/COHORT_ME_QEATS_ENROLL_CSOD_2019/chiragmittalcm7-me_qeats.git /var/local/git/qeats-server
RUN cd /var/local/git/qeats-server && gradle build -x test --stop --parallel
EXPOSE 8081
CMD ["/bin/sh", "-c", "gradle bootrun --parallel -p /var/local/git/qeats-server 2>&1 | tee /tmp/qeats-server.log"]

# TIP(MODULE_DEPLOYMENT): Useful commands.
# sudo docker build -t qeats-server .
# sudo docker run -m 800m -v /tmp/container:/tmp:rw -p 80:8081 qeats-server
