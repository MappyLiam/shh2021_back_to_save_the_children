# sigma-stm32-mask-project

본 Repository는 SeoulHardwareHackathon2020에서 __마스크 잘쓰니__ 제품의 STM보드 코드 작성 및 협업을 위해 만들어졌다.


# 개발 시 참고사항
## Wi-Fi
Code Generate시, 아래 부분을 수정해주어야 한다. es_wifi_io.c에서 hspi를 통해 wifi init을 진행하기 때문이다.
```
~/stm32l4xx_it.c
void SPI3_IRQHandler(void)
{
  // TODO: Set &hspi only. it is configured for wifi connection in es_wifi_io.c
  HAL_SPI_IRQHandler(&hspi3); // remove it
  HAL_SPI_IRQHandler(&hspi); // we need only this one
}

```
