FROM python:3.7
WORKDIR /usr/app

COPY ./requirements.txt .
RUN pip install -r requirements.txt

COPY . .

ENTRYPOINT ["python", "server.py"]