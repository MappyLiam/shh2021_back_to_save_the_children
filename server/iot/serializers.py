from rest_framework import serializers

from .models import MaskWearData, FineDustData, CO2Data, TVOCData

class MaskWearDataSerializer(serializers.ModelSerializer):
    datetime = serializers.DateTimeField()
    class Meta:
        model = MaskWearData
        fields = ['datetime', 'wearing']


class FineDustDataSerializer(serializers.ModelSerializer):
    datetime = serializers.DateTimeField()
    class Meta:
        model = FineDustData
        fields = ['datetime', 'pm2_5', 'pm10']


class CO2DataSerializer(serializers.ModelSerializer):
    datetime = serializers.DateTimeField()
    class Meta:
        model = CO2Data
        fields = ['datetime', 'ppm']


class TVOCDataSerializer(serializers.ModelSerializer):
    datetime = serializers.DateTimeField()
    class Meta:
        model = TVOCData
        fields = ['datetime', 'ppb']


class TimeSeriesDataSerializer(serializers.Serializer):
    num_of_data = serializers.IntegerField(required=False)
    mask_wear_data = MaskWearDataSerializer(many=True)
    fine_dust_data = FineDustDataSerializer(many=True)
    CO2_data = CO2DataSerializer(many=True)
    TVOC_data = TVOCDataSerializer(many=True)


class OneDataSerializer(serializers.Serializer):
    mask_wear_data = MaskWearDataSerializer()
    fine_dust_data = FineDustDataSerializer()
    CO2_data = CO2DataSerializer()
    TVOC_data = TVOCDataSerializer()



