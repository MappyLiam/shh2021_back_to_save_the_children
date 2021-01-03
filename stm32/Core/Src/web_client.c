/* Includes ------------------------------------------------------------------*/
#include "web_client.h"

/* Private defines -----------------------------------------------------------*/

/* Update SSID and PASSWORD with own Access point settings */
//#define SSID     "SIGMA5"
//#define PASSWORD "#define SIGMA 51939"

#define SSID     "BeTheBest"
#define PASSWORD "itsmeliam"

//uint8_t RemoteIP[] = {13,125,25,251}; // EB instance
//#define RemotePORT	80
uint8_t RemoteIP[] = {192,168,43,209}; // localhost
#define RemotePORT	8000 // for localhost

uint8_t MAC_Addr[6];
uint8_t IP_Addr[4];

#define WIFI_WRITE_TIMEOUT 10000
#define WIFI_READ_TIMEOUT  10000

#define CONNECTION_TRIAL_MAX          10




Web_Client_Status_t Web_Client_Init(){
  TERMOUT("\n****** WIFI Module in TCP Client mode demonstration ****** \n\n");
  TERMOUT("TCP Client Instructions :\n");
  TERMOUT("1- Make sure your Phone is connected to the same network that\n");
  TERMOUT("   you configured using the Configuration Access Point.\n");
  TERMOUT("2- Create a server by using the android application TCP Server\n");
  TERMOUT("   with port(8002).\n");
  TERMOUT("3- Get the Network Name or IP Address of your Android from the step 2.\n\n");

  /*Initialize  WIFI module */
  if(WIFI_Init() ==  WIFI_STATUS_OK)
  {
	TERMOUT("> WIFI Module Initialized.\n");
	if(WIFI_GetMAC_Address(MAC_Addr) == WIFI_STATUS_OK)
	{
	  TERMOUT("> es-wifi module MAC Address : %X:%X:%X:%X:%X:%X\n",
			   MAC_Addr[0],
			   MAC_Addr[1],
			   MAC_Addr[2],
			   MAC_Addr[3],
			   MAC_Addr[4],
			   MAC_Addr[5]);
	}
	else
	{
	  TERMOUT("> ERROR : CANNOT get MAC address\n");
//	  BSP_LED_On(LED2);
	}

	if( WIFI_Connect(SSID, PASSWORD, WIFI_ECN_WPA2_PSK) == WIFI_STATUS_OK)
	{
	  TERMOUT("> es-wifi module connected \n");
	  if(WIFI_GetIP_Address(IP_Addr) == WIFI_STATUS_OK)
	  {
		TERMOUT("> es-wifi module got IP Address : %d.%d.%d.%d\n",
			   IP_Addr[0],
			   IP_Addr[1],
			   IP_Addr[2],
			   IP_Addr[3]);
		if (Web_Client_Connect() == WEB_CLIENT_STATUS_OK) {
			return WEB_CLIENT_STATUS_OK;
		}
	  }
	  else
	  {
		TERMOUT("> ERROR : es-wifi module CANNOT get IP address\n");
	  }
	}
	else
	{
	  TERMOUT("> ERROR : es-wifi module NOT connected\n");
	}
  }
  else
  {
	TERMOUT("> ERROR : WIFI Module cannot be initialized.\n");
  }
  return WEB_CLIENT_STATUS_ERROR;
}

Web_Client_Status_t Web_Client_Connect() {
	int16_t Trials = CONNECTION_TRIAL_MAX;
//	TERMOUT("> Trying to connect to Host: %s ...\n",
//			   RemoteHostAddr);

	TERMOUT("> Trying to connect to Server: %d.%d.%d.%d:%d ...\n",
		   RemoteIP[0],
		   RemoteIP[1],
		   RemoteIP[2],
		   RemoteIP[3],
						 RemotePORT);

	while (Trials--)
	{
//	  if( WIFI_GetHostAddress(RemoteHostAddr, RemoteIP) == WIFI_STATUS_OK) {
//		  TERMOUT("> Found Host IP using DNS.\n");
		  if( WIFI_OpenClientConnection(0, WIFI_TCP_PROTOCOL, "TCP_CLIENT", RemoteIP, RemotePORT, 0) == WIFI_STATUS_OK)
		  {
			TERMOUT("> TCP Connection opened successfully.\n");
			return WEB_CLIENT_STATUS_OK;
		  }
//	  }
	}
    TERMOUT("> ERROR : Cannot open Connection\n");
	return WEB_CLIENT_STATUS_ERROR;
}

Web_Client_Status_t Web_Client_Disconnect(uint8_t Socket) {
	if (Socket != -1) {
		TERMOUT("> Trying to disconnect to Server ...\n");
		int32_t ret = WIFI_CloseClientConnection(Socket);
		if (ret != WIFI_STATUS_OK) {
			TERMOUT("> ERROR : Cannot close Connection\n");
			return WEB_CLIENT_STATUS_ERROR;
		}
		TERMOUT("> TCP Connection closed successfully.\n");
	}
	return WEB_CLIENT_STATUS_OK;
}

Web_Client_Status_t Web_Visit(uint8_t Socket, uint8_t *TxData, uint16_t Reqlen) {
  Web_Client_Disconnect(Socket);

  if(Web_Client_Connect() == WEB_CLIENT_STATUS_OK)
  {
	Socket = 0;
    int32_t ret;
//    uint8_t * RxData;
    uint8_t RxData [1000];
    uint16_t Datalen;
	ret = WIFI_SendData(Socket, TxData, Reqlen, &Datalen, WIFI_WRITE_TIMEOUT);
	if(ret == WIFI_STATUS_OK)
	{
	  if(Datalen > 0)
	  {
		TxData[Datalen]=0;
		TERMOUT("Sent: %s\n",TxData);

		bool received = false;
		while (WIFI_ReceiveData(Socket, RxData, Reqlen-1, &Datalen, WIFI_READ_TIMEOUT) == WIFI_STATUS_OK){
			if (!received) {
				TERMOUT("=== Received === \n");
				received = true;
			}
			RxData[Datalen]=0;
			TERMOUT("%s", RxData);
			HAL_Delay(100);
		}
		if (received) {
			TERMOUT("\n============== \n\n");
		}
	  }
	  return WEB_CLIENT_STATUS_OK;
	}
	else
	{
	  TERMOUT("> ERROR : Failed to Send Data, connection closed\n");
	}
  }
  return WEB_CLIENT_STATUS_ERROR;
}




