from django.contrib import admin

from iot.models import MaskWearData, FineDustData, CO2Data, TVOCData
# Register your models here.

class MaskWearDataAdmin(admin.ModelAdmin):
    readonly_fields = ('datetime', 'wearing',)
    fields = ['datetime', 'wearing']
admin.site.register(MaskWearData, MaskWearDataAdmin)


class FineDustDataAdmin(admin.ModelAdmin):
    readonly_fields = ('datetime', 'pm2_5', 'pm10',)
    fields = ['datetime', 'pm2_5', 'pm10']
admin.site.register(FineDustData, FineDustDataAdmin)


class CO2DataAdmin(admin.ModelAdmin):
    readonly_fields = ('datetime', 'ppm',)
    fields = ['datetime', 'ppm']
admin.site.register(CO2Data, CO2DataAdmin)


class TVOCDataAdmin(admin.ModelAdmin):
    readonly_fields = ('datetime', 'ppb',)
    fields = ['datetime', 'ppb']
admin.site.register(TVOCData, TVOCDataAdmin)
