FROM python:3.7-alpine
WORKDIR /usr/app

COPY ./requirements.txt
RUN pip install -r requirements.txt

COPY ./model .
COPY ./model.pt .
COPY ./server.py .
COPY ./service-account.json .

ENTRYPOINT ["python", "server.py"]