#!/usr/bin/python
# -*- coding: utf-8 -*-

# Connect to a websocket powered by blockchain.info and print events in

# the terminal in real time.

import json

from time import time
from kafka import KafkaProducer

import websocket  # install this with the following command: pip install websocket-client


def main():

    ws = open_websocket_to_blockchain()

    last_ping_time = time()

    producer = KafkaProducer(bootstrap_servers='localhost:9092')

    while True:

        # Receive event

        data = json.loads(ws.recv())

        # We ping the server every 10s to show we are alive

        if time() - last_ping_time >= 10:

            ws.send(json.dumps({'op': 'ping'}))

            last_ping_time = time()

        # Response to "ping" events

        if data['op'] == 'pong':

            pass
        elif data['op'] == 'utx':

        # New unconfirmed transactions

            utxDict = {}

            transaction_timestamp = data['x']['time']

            transaction_hash = data['x']['hash']  # this uniquely identifies the transaction

            transaction_total_amount = 0

            utxDict['transaction_timestamp'] = transaction_timestamp
            utxDict['transaction_hash'] = transaction_hash

            for recipient in data['x']['out']:

                # Every transaction may in fact have multiple recipients

                # Note that the total amount is in hundredth of microbitcoin; you need to

                # divide by 10**8 to obtain the value in bitcoins.

                transaction_total_amount += recipient['value'] / 100000000.

            utxDict['transaction_total_amount'] = transaction_total_amount

            producer.send('utx', json.dumps(utxDict).encode())

            print ('{} New transaction {}: {} BTC'.format(transaction_timestamp, transaction_hash, transaction_total_amount))
        else:

            print ('Unknown op: {}'.format(data['op']))


def open_websocket_to_blockchain():

    # Open a websocket

    ws = websocket.WebSocket()

    ws.connect('wss://ws.blockchain.info/inv')

    # Register to unconfirmed transaction events

    ws.send(json.dumps({'op': 'unconfirmed_sub'}))

    # Register to block creation events

    ws.send(json.dumps({'op': 'blocks_sub'}))

    return ws


if __name__ == '__main__':

    main()
