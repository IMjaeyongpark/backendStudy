import time
import random
import sys
from flask import Flask, Response
from flask_cors import CORS
from datetime import timedelta

import requests as requests
import json
import pytchat
import pafy
import re
from werkzeug.exceptions import ClientDisconnected
from datetime import datetime
from googleapiclient.discovery import build
from flask_restful import Resource, Api
from Po import Po
from dotenv import load_dotenv
import os


def create_app():
    app = Flask(__name__)
    return app

app = create_app()


api = Api(app)
app.config['JSON_AS_ASCII'] = False
CORS(app)

running = True
print("실행레쓰고")

load_dotenv()
youtube_api_key = os.environ.get('youtube_api_key')
topic_IP = os.environ.get('topic_IP')

# 인기 급상승 10위
api.add_resource(Po, '/po')




# 실시간 구독자 수
@app.route('/subcnt/<channel_ID>')
def subcnt(channel_ID):
    youtube = build('youtube', 'v3', developerKey=youtube_api_key)
    request = youtube.channels().list(
        part='statistics',
        id=channel_ID
    )
    response = request.execute()

    if 'items' in response:
        statistics = response['items'][0]['statistics']
        subscriber_count = statistics['subscriberCount']
        return subscriber_count
    else:
        return "400"


# 유튜브 실시간 댓글 분석
@app.route('/live/<BCID>/<Email>')
def sse(BCID, Email):
    def generate(BCID, Email):

        chat = pytchat.create(video_id=BCID)

        # 방송 시작시간 가져오기
        video_url = 'https://www.googleapis.com/youtube/v3/videos'
        video_params = {
            'part': 'snippet',
            'id': BCID,
            'key': youtube_api_key
        }
        video_r = requests.get(video_url, video_params)
        video_data = json.loads(json.dumps(video_r.json()))
        published = video_data["items"][0]["snippet"]["publishedAt"]
        # datetime.timedelta타입으로 변환
        published = datetime.strptime(published, '%Y-%m-%dT%H:%M:%SZ')
        preName = ""
        preDate = ""
        while chat.is_alive():
            try:
                data = chat.get()
                items = data.items
                for c in items:
                    if not (preDate == c.datetime and preName == c.author.name):
                        mes = re.sub(r':[^:]+:', '', c.message)
                        print(mes)


                        """data2 = {
                            "author": c.author.name,
                            "dateTime": c.datetime,
                            "message": mes,
                            "emotion3": random.randint(0, 2),
                            "emotion7": random.randint(0, 6)
                        }
                        yield f'data:{data2}\n\n''"""

                        IP = os.environ.get('server_IP')
                        emotion = requests.get(
                            IP + c.message
                        ).json()
                        emotion['emotion7'] = int(emotion['emotion7'])
                        emotion['emotion3'] = float(emotion['emotion3'])
                        if emotion['emotion7'] == 4 or (emotion['emotion3'] > 0.45 and emotion['emotion3'] < 0.55):
                            emotion['emotion3'] = 2
                        elif emotion['emotion3'] > 0.5:
                            emotion['emotion3'] = 1
                        else:
                            emotion['emotion3'] = 0

                        data2 = {
                            "author": c.author.name,
                            "dateTime": c.datetime,
                            "message": mes,
                            "emotion3": emotion['emotion3'],
                            "emotion7": emotion['emotion7']
                        }
                        yield f"data:{data2}\n\n"
                        URI = "http://localhost:8080/chat?email=" + Email + "&BCID=" + BCID + "&name=" + c.author.name
                        do_async(URI, data2)
                        preName = c.author.name
                        preDate = c.datetime

            except ClientDisconnected:
                print("클라이언트 연결 종료")
                break
    print("zz")
    return Response(generate(BCID, Email), mimetype='text/event-stream')

async def do_async(URI,data2):
    header = {"Content-type": "application/json", "Accept": "text/plain"}
    requests.get(URI, data=json.dumps(data2), headers=header)
    print("완료!")

# 시청자 수
@app.route('/concurrentViewers/<BCID>')
def concurrentViewers(BCID):
    def viewer(BCID):
        header = {"Content-type": "application/json", "Accept": "text/plain"}
        URI = "https://www.googleapis.com/youtube/v3/videos?id=" + BCID + \
              "&key=" + youtube_api_key + "&part=snippet,contentDetails,statistics,status"
        res = requests.get(URI).json()
        published = datetime.strptime(res['items'][0]['snippet']['publishedAt'], '%Y-%m-%dT%H:%M:%SZ')
        url = f'https://www.googleapis.com/youtube/v3/videos?id={BCID}&key={youtube_api_key}&part=liveStreamingDetails'

        while 1:
            # YouTube API 호출
            response = requests.get(url)
            data = response.json()
            vi = data['items'][0]['liveStreamingDetails']['concurrentViewers']
            yield f"data:{vi}\n\n"
            t = datetime.now() - published
            spurl = f'http://localhost:8080/saveViewer?BCID={BCID}&sec={t.seconds}&viewer={vi}'
            requests.get(spurl, headers=header)
            time.sleep(15)

    return Response(viewer(BCID), mimetype='text/event-stream')


@app.route("/feedback/<BCID>")
def feedback(BCID):
    URI = f'http://localhost:8080/getChat?BCID={BCID}'
    data = requests.get(URI).json()
    published = datetime.strptime(data['published'], '%Y-%m-%dT%H:%M:%SZ')
    published = published + timedelta(hours=9)
    time_data = []
    emotion7 = [0, 0, 0, 0, -999999, 0, 0]
    for key, value in data['viewer'].items():
        time_data.append((int(key) - 32400, [0, 0, [0, 0, 0, 0, 0, 0, 0]]))

    time_data = sorted(time_data)
    for item in data['cd']:
        t = datetime.strptime(item['dateTime'], '%Y-%m-%d %H:%M:%S')
        sec = (t - published).seconds
        pre = 0
        for key, val in enumerate(time_data):
            if sec < val[0]:
                if item['emotion3'] == 0 or item['emotion3'] == 1:
                    time_data[pre][1][item['emotion3']] += 1
                time_data[pre][1][2][item['emotion7']] += 1
                emotion7[item['emotion7']] += 1
                break
            pre = key

    min_Viewr = int(jsonmax(data['viewer']) * 0.7)
    max_emo7 = emotion7.index(max(emotion7))
    emo7_max = 0
    emo7_idx = 0
    po_emo = 0
    po_idx = 0
    na_emo = 0
    na_idx = 0
    for item in time_data:
        if int(item[0]) > min_Viewr:
            if item[1][1] > po_emo:
                po_emo = item[1][1]
                po_idx = item[0]
            if item[1][0] > na_emo:
                na_emo = item[1][0]
                na_idx = item[0]
            if item[1][2][max_emo7] > emo7_max:
                emo7_max = item[1][2][max_emo7]
                emo7_idx = item[0]

    # URI = f'{topic_IP}nAK6IWev38E'
    # print(requests.get(URI).json())
    data = {
        "positive": str(po_idx),
        "negative": str(na_idx),
        "emotion7": (max_emo7, emo7_idx)
    }
    return json.dumps(data)


def jsonmax(data):
    max_value = max(data.values())
    index = 0
    for key, value in data.items():
        if value == max_value:
            return index
        index += 1


if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True, port=8801, threaded=False, processes=10)
