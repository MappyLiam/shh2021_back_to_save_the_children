import pytz
from datetime import datetime, timedelta
import random

from django.core.management.base import BaseCommand, CommandError

from iot.models import MaskWearData, FineDustData, CO2Data, TVOCData

class Command(BaseCommand):

    def handle(self, *args, **kwargs):
        time_interval = 1 # in minute
        total = 60*24*30*3 / time_interval
        seoul_tz = pytz.timezone('Asia/Seoul')
        now = seoul_tz.localize(datetime.now())
        MaskWearData.objects.all().delete()
        FineDustData.objects.all().delete()
        CO2Data.objects.all().delete()

        p_5 = total / 20
        progress = 0


        while (progress < total):
            time = progress * time_interval

            wearing = random.randint(0, 1)

            mask = MaskWearData.objects.create(
                wearing = True if wearing else False,
            )
            MaskWearData.objects.filter(id=mask.id).update(
                datetime = now - timedelta(minutes=time)
            )

            dust = FineDustData.objects.create(
                pm2_5 = random.randint(10, 40),
                pm10 = random.randint(40, 80),
            )
            FineDustData.objects.filter(id=dust.id).update(
                datetime = now - timedelta(minutes=time)
            )

            co2 = CO2Data.objects.create(
                ppm = random.randint(500, 1200),
            )
            CO2Data.objects.filter(id=co2.id).update(
                datetime = now - timedelta(minutes=time)
            )

            tvoc = TVOCData.objects.create(
                ppb = random.randint(50, 300),
            )
            TVOCData.objects.filter(id=tvoc.id).update(
                datetime = now - timedelta(minutes=time)
            )

            progress += 1
            if progress % p_5 == 0:
                print(f'progress: {progress}/{total}')


