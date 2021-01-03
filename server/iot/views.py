from django.shortcuts import render
from django.http import HttpResponse

from iot.models import MaskWearData, FineDustData, TVOCData, CO2Data

# Create your views here.
def index(request):
    """it's for receiving iot sensor value"""
    pm2_5 = request.GET.get('pm2_5', 0)
    pm10 = request.GET.get('pm10', 0)
    mask_wear = request.GET.get('mask_wear', False)

    MaskWearData.objects.create(
        wearing = mask_wear,
    )
    FineDustData.objects.create(
        pm2_5 = pm2_5,
        pm10 = pm10,
    )

    return HttpResponse("OK!")

