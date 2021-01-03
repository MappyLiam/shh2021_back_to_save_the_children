/*

 * PMSa003.h
 *
 *  Created on: 2020. 12. 28.
 *      Author: user
*/


#ifndef INC_PMSA003_H_
#define INC_PMSA003_H_

typedef enum {
	no_error,
	error
}Error;

void UpdateData_PM(void);
void Command_PMSa003(char*);
uint16_t GetData_PM10(void);
uint16_t GetData_PM25(void);
HAL_StatusTypeDef Trans_State();
HAL_StatusTypeDef Receive_State();
Error Uart_not_Ready();
Error Is_Error();

#endif  /*INC_PMSA003_H_*/

