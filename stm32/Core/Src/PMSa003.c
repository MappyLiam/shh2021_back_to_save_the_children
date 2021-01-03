/*
 * PMSa003.c
 *
 *  Created on: 2020. 12. 28.
 *      Author: user
*/
#include <string.h>
#include "main.h"
#include "PMSa003.h"

extern UART_HandleTypeDef huart4;



typedef struct PMS{
	Error uart_not_ready1;
	Error uart_not_ready2;
	HAL_StatusTypeDef transmit_state;
	HAL_StatusTypeDef receive_state;
	uint16_t PM10;
	uint16_t PM25;
}PMS;

uint8_t PMSa003_Receive[32];
uint8_t PMSa003_Send[7]={0x42,0x4d,0};

PMS pmsa003;

void UpdateData_PM(void)
{
	uint16_t receive_checker, sum=0;

	receive_checker=PMSa003_Receive[30]<<8|PMSa003_Receive[31];
	for(int i=0;i<30;i++)
	{
		sum+=PMSa003_Receive[i];
	}

	if(receive_checker==sum)
	{
		pmsa003.PM25=(PMSa003_Receive[12]<<8)|PMSa003_Receive[13];
		pmsa003.PM10=(PMSa003_Receive[14]<<8)|PMSa003_Receive[15];
	}
}

void Command_PMSa003(char* cmd)
{
	uint16_t check=0;

	if(strcmp(cmd,"Passive_Read")==0)
	{
		PMSa003_Send[2]=0xe2;
		PMSa003_Send[3]=0x00;
		PMSa003_Send[4]=0x00;
	}
	else if(strcmp(cmd,"Passive_Mode")==0)
	{
		PMSa003_Send[2]=0xe1;
		PMSa003_Send[3]=0x00;
		PMSa003_Send[4]=0x00;
	}

	for(int i=0;i<5;i++)
	{
		check+=PMSa003_Send[i];
	}

	PMSa003_Send[5]=check>>8;
	PMSa003_Send[6]=check;

	if(HAL_UART_GetState(&huart4)!=HAL_UART_STATE_READY)
	{
		pmsa003.uart_not_ready1=error;
	}
	pmsa003.transmit_state=HAL_UART_Transmit(&huart4,(uint8_t*)PMSa003_Send,7,1000);

	if(strcmp(cmd,"Passive_Read")==0)
	{
		if(HAL_UART_GetState(&huart4)!=HAL_UART_STATE_READY)
		{
			pmsa003.uart_not_ready2=error;
		}
		pmsa003.receive_state=HAL_UART_Receive(&huart4,(uint8_t*)PMSa003_Receive,32,1000);
	}
}
uint16_t GetData_PM10()
{
	return pmsa003.PM10;
}
uint16_t GetData_PM25()
{
	return pmsa003.PM25;
}

Error Uart_not_Ready()
{
	return pmsa003.uart_not_ready1 | pmsa003.uart_not_ready2;
}

HAL_StatusTypeDef Trans_State()
{
	return pmsa003.transmit_state;
}

HAL_StatusTypeDef Receive_State()
{
	return pmsa003.receive_state;
}
Error Is_Error()
{
	if(Uart_not_Ready() | (Trans_State()!=HAL_OK) | (Receive_State()!=HAL_OK)) return error;
	else return no_error;
}
