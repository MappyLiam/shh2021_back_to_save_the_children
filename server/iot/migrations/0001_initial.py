# Generated by Django 3.1.4 on 2020-12-29 09:04

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='CO2Data',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('datetime', models.DateTimeField(auto_now_add=True)),
                ('ppm', models.IntegerField(default=0)),
            ],
        ),
        migrations.CreateModel(
            name='FineDustData',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('datetime', models.DateTimeField(auto_now_add=True)),
                ('pm2_5', models.IntegerField(default=0)),
                ('pm10', models.IntegerField(default=0)),
            ],
        ),
        migrations.CreateModel(
            name='MaskWearData',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('datetime', models.DateTimeField(auto_now_add=True)),
                ('wearing', models.BooleanField()),
            ],
        ),
    ]