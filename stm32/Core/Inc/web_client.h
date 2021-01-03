#ifndef WEB_CLIENT_H
#define WEB_CLIENT_H


#if !defined (TERMINAL_USE)
#define TERMINAL_USE
#endif

#if defined (TERMINAL_USE)
#define TERMOUT(...)  printf(__VA_ARGS__)
#else
#define TERMOUT(...)
#endif


#ifdef __cplusplus
 extern "C" {
#endif


/* Includes ------------------------------------------------------------------*/
#include "wifi.h"
/* Private defines -----------------------------------------------------------*/

/* Exported constants --------------------------------------------------------*/

/* Exported types ------------------------------------------------------------*/
typedef enum {
  WEB_CLIENT_STATUS_OK             = 0,
  WEB_CLIENT_STATUS_ERROR          = 1,
  WEB_CLIENT_STATUS_NOT_SUPPORTED  = 2,
  WEB_CLIENT_STATUS_JOINED         = 3,
  WEB_CLIENT_STATUS_ASSIGNED       = 4,
  WEB_CLIENT_STATUS_TIMEOUT        = 5,
}Web_Client_Status_t;

/* Exported macro ------------------------------------------------------------*/
/* Exported functions ------------------------------------------------------- */
Web_Client_Status_t		  Web_Client_Init();
Web_Client_Status_t  	  Web_Client_Connect();
Web_Client_Status_t 	  Web_Client_Disconnect(uint8_t socket);
Web_Client_Status_t		  Web_Visit(uint8_t socket, uint8_t *pdata, uint16_t Reqlen);
Web_Client_Status_t       Web_Client_Read(uint8_t socket, uint8_t *pdata, uint16_t Reqlen, uint16_t *RcvDatalen, uint32_t Timeout);
Web_Client_Status_t       Web_Client_Write(uint8_t socket, uint8_t *pdata, uint16_t Reqlen, uint16_t *RcvDatalen, uint32_t Timeout);


#ifdef __cplusplus
}
#endif

#endif /* WEB_CLIENT_H */
