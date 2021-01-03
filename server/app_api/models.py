from django.db import models
from django.contrib.auth.models import (
                                            BaseUserManager, AbstractBaseUser, PermissionsMixin, AbstractUser

)
from django.core.exceptions import ValidationError


class User(AbstractUser):
    pass

