import pytz
import itertools
from datetime import datetime, timedelta
from dateutil.relativedelta import relativedelta
from django.shortcuts import render
from django.db.models import Q
from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from drf_yasg.utils import get_serializer_class, swagger_auto_schema
from drf_yasg import openapi

from iot.models import MaskWearData, FineDustData, CO2Data, TVOCData
from iot.serializers import MaskWearDataSerializer, FineDustDataSerializer, CO2DataSerializer, TVOCDataSerializer, TimeSeriesDataSerializer, OneDataSerializer

# Create your views here.
def get_date_filter(interval):
    today = datetime.today()
    if interval=='today':
        from_date = today
    elif interval=='week':
        from_date = today - timedelta(days=6)
    elif interval=='month':
        from_date = today - relativedelta(months=1)
    return Q(datetime__date__gte=from_date)

def group_datetime_within(dt, criteria):
    if 'h' in criteria:
        divide_criteria = int(criteria.split('h')[0])
        date = dt.strftime("%x")
        hour = dt.strftime("%H")
        return f'{date} {int(int(hour)/divide_criteria)}'
    elif 'm' in criteria:
        divide_criteria = int(criteria.split('m')[0])
        date_hour = dt.strftime("%x %H")
        minute = dt.strftime("%M")
        return f'{date_hour} {int(int(minute)/divide_criteria)}'

def extract_data_in_interval(data, interval):
    interval_map = [
        ('today', '5m'),
        ('week', '30m'),
        ('month', '2h'),
    ]
    extracted_data = []
    groups = itertools.groupby(data, lambda x:group_datetime_within(x.datetime, dict(interval_map)[interval]))
    for k, g in groups:
        for thing in g:
            extracted_data.append(thing)
            break
    extracted_data = sorted(extracted_data, key=lambda x: x.datetime)
    return extracted_data

def get_time_series_data(interval=None, latest=False):
    if latest:
        is_many = True
        mask_id = MaskWearData.objects.order_by('datetime').last().id
        mask_wear_data = list(MaskWearData.objects.filter(id=mask_id))
        dust_id = FineDustData.objects.order_by('datetime').last().id
        fine_dust_data = list(FineDustData.objects.filter(id=dust_id))
        co2_id = CO2Data.objects.order_by('datetime').last().id
        CO2_data = list(CO2Data.objects.filter(id=co2_id))
        tvoc_id = TVOCData.objects.order_by('datetime').last().id
        TVOC_data = list(TVOCData.objects.filter(id=tvoc_id))
    else:
        date_filter = get_date_filter(interval)
        is_many = True
        mask_wear_objs = MaskWearData.objects.filter(date_filter)
        mask_wear_data = extract_data_in_interval(mask_wear_objs, interval)
        fine_dust_objs= FineDustData.objects.filter(date_filter)
        fine_dust_data = extract_data_in_interval(fine_dust_objs, interval)
        CO2_objs = CO2Data.objects.filter(date_filter)
        CO2_data = extract_data_in_interval(CO2_objs, interval)
        TVOC_objs = TVOCData.objects.filter(date_filter)
        TVOC_data = extract_data_in_interval(TVOC_objs, interval)
    serialized_mask_wear_data = MaskWearDataSerializer(mask_wear_data, many=is_many).data
    serialized_fine_dust_data = FineDustDataSerializer(fine_dust_data, many=is_many).data
    serialized_CO2_data = CO2DataSerializer(CO2_data, many=is_many).data
    serialized_TVOC_data = TVOCDataSerializer(TVOC_data, many=is_many).data
    time_series_data = {
        'mask_wear_data': serialized_mask_wear_data,
        'fine_dust_data': serialized_fine_dust_data,
        'CO2_data': serialized_CO2_data,
        'TVOC_data': serialized_TVOC_data,
    }
    return time_series_data

class NowData(APIView):
    def get(self, request):
        time_series_data = get_time_series_data(latest=True)
        serializer = TimeSeriesDataSerializer(data=time_series_data)

        if serializer.is_valid():
            return Response(serializer.validated_data, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class TimeSeriesData(APIView):
    @swagger_auto_schema(
        operation_description="""choose interval in today, week, month""",
        manual_parameters=[openapi.Parameter(
                        'interval',
                        openapi.IN_QUERY,
                        type=openapi.TYPE_STRING,
        )],
    )
    def get(self, request):
        interval = request.GET.get('interval')
        time_series_data = get_time_series_data(interval)
        time_series_data['num_of_data'] = len(time_series_data["mask_wear_data"])
        serializer = TimeSeriesDataSerializer(data=time_series_data)


        if serializer.is_valid():
            return Response(serializer.validated_data, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

