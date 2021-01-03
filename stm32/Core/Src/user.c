/*
 * user.c
 *
 *  Created on: Dec 28, 2020
 *      Author: mappyliam
 */

#include "main.h"
#include "user.h"

extern UART_HandleTypeDef huart1;

extern uint32_t saved_pressure_value;
extern uint32_t pressure_value;
extern ADC_HandleTypeDef hadc1;

void adcwork(){
   HAL_ADC_Start(&hadc1);
   HAL_ADC_PollForConversion(&hadc1, 100);
   pressure_value = HAL_ADC_GetValue(&hadc1);
   HAL_Delay(1);
}
