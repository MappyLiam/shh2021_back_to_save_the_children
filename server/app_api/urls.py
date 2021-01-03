from django.urls import path

from .views import TimeSeriesData, NowData

urlpatterns = [
    path('now_data', NowData.as_view(), name="now_data"),
    path('time_series_data', TimeSeriesData.as_view(), name='time_series_data'),
]

