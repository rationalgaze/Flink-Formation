#!/usr/bin/python
# -*- coding: utf-8 -*-

# Connect to a websocket powered by blockchain.info and print events in

# the terminal in real time.

import json

import requests
import json

from kafka import KafkaProducer

import time
     


def main():

    while True:
    
        producer = KafkaProducer(bootstrap_servers='localhost:9092')

        url = "http://api.coindesk.com/v1/bpi/currentprice.json"

        response = json.loads(requests.get(url).text)

        print(response)
          
        producer.send('taux', json.dumps(response).encode())
        
        time.sleep(2)

if __name__ == '__main__':

    main()
