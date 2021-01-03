from django.db import models

# Create your models here.
class MaskWearData(models.Model):
    datetime = models.DateTimeField(auto_now_add=True)
    wearing = models.BooleanField()


class FineDustData(models.Model):
    datetime = models.DateTimeField(auto_now_add=True)
    pm2_5 = models.IntegerField(default=0)
    pm10 = models.IntegerField(default=0)


class CO2Data(models.Model):
    datetime = models.DateTimeField(auto_now_add=True)
    ppm = models.IntegerField(default=0)


class TVOCData(models.Model):
    datetime = models.DateTimeField(auto_now_add=True)
    ppb = models.IntegerField(default=0)
