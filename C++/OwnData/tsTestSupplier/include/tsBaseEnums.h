#pragma once

#include <wtypes.h>

// --------------------------------------------------------------------------------------
// tagTSCONNECTIONSTATUS - Status of connection
// --------------------------------------------------------------------------------------
enum tagTSCONNECTIONSTATUS
{
  TS_CONNECTION_NOTHING = -1,
  TS_CONNECTION_OFFLINE = 0,
  TS_CONNECTION_ONLINE = 1,
  TS_CONNECTION_CONNECTING = 2
};

// --------------------------------------------------------------------------------------
// tagTSCATEGORIES - Categories of Symbol
// --------------------------------------------------------------------------------------
enum tagTSCATEGORIES
{
  TS_CATEGORY_NOTHING = 254,
  TS_CATEGORY_FUTURE = 0,
  TS_CATEGORY_FUTUREOPTION = 1,
  TS_CATEGORY_STOCK = 2,
  TS_CATEGORY_STOCKOPTION = 3,
  TS_CATEGORY_INDEX = 4,
  TS_CATEGORY_CURROPTION = 5,
  TS_CATEGORY_MUTUALFUND = 6,
  TS_CATEGORY_MONEYMKTFUND = 7,
  TS_CATEGORY_INDEXOPTION = 8,
  TS_CATEGORY_CASH = 9,
  TS_CATEGORY_BOND = 10,
  TS_CATEGORY_SPREAD = 11,
  TS_CATEGORY_FOREX = 12,
  TS_CATEGORY_CPCSYMBOL = 13,
  TS_CATEGORY_COMPOSITE = 14,
  TS_CATEGORY_FUTUREROOT = 15,
  TS_CATEGORY_FUTUREOPTIONROOT = 16,
  TS_CATEGORY_STOCKOPTIONROOT = 17
};

// --------------------------------------------------------------------------------------
// tagTSRESOLUTIONS - Resolutions of quota
// --------------------------------------------------------------------------------------
enum tagTSRESOLUTIONS
{
  TS_RESOLUTION_UNKNOWN = 0,
  TS_RESOLUTION_TICK = 1,
  TS_RESOLUTION_SECOND = 9,
  TS_RESOLUTION_MINUTE = 2,
  TS_RESOLUTION_HOUR = 3,
  TS_RESOLUTION_DAY = 4,
  TS_RESOLUTION_WEEK = 5,
  TS_RESOLUTION_MONTH = 6,
  TS_RESOLUTION_YEAR = 7,
  TS_RESOLUTION_VALUE = 8,
  TS_RESOLUTION_QUARTER = 10,
  TS_RESOLUTION_POINTS = 11
};

// --------------------------------------------------------------------------------------
// tagTSFIELDS - Fields
// --------------------------------------------------------------------------------------
enum tagTSFIELDS
{
  TS_BID_FIELD = 200,
  TS_TRADE_FIELD = 201,
  TS_ASK_FIELD = 202,
  TS_SPLIT_FIELD = 203,
  TS_DISTRIBUTION_FIELD = 204,
  TS_PRICE_MULTIPLIER_FIELD = 205,
  TS_VOLUME_MULTIPLIER_FIELD = 206,
  TS_BEST_BID_FIELD = 254,
  TS_BEST_ASK_FIELD = 255,
  TS_IMPLIED_VOLATILITY_FIELD = 279
};

// --------------------------------------------------------------------------------------
// tagTSBARSTATUS - Bitmap for status of bar
//
// RULES:
//
// TICKS:   
//           HISTORY MODE: (0x0D) TS_BAR_OPEN|TS_BAR_CLOSE|TS_BAR_HISTORICAL_DATA 
//                   (0x10000015) TS_BAR_TECH_BAR|TS_BAR_OPEN|TS_BAR_HISTORICAL_DATA|TS_BAR_END_OF_SESSION_CLOSE - ��������� �����������(������) Tick � ������

//          REALTIME MODE: (0x0B) TS_BAR_OPEN|TS_BAR_CLOSE|TS_BAR_REALTIME_DATA 
//                   (0x10000013) TS_BAR_TECH_BAR|TS_BAR_OPEN|TS_BAR_REAL_TIME_DATA|TS_BAR_END_OF_SESSION_CLOSE - ��������� �����������(������) Tick � ������


// BARS:
//           HISTORY MODE: 
//               OPEN BAR: (0x05) TS_BAR_OPEN|TS_BAR_HISTORICAL_DATA
//             INSIDE BAR: (0x05) TS_BAR_OPEN|TS_BAR_HISTORICAL_DATA 
//              CLOSE BAR: (0x0D) TS_BAR_OPEN|TS_BAR_CLOSE|TS_BAR_HISTORICAL_DATA 
//                         (0x1D) TS_BAR_OPEN|TS_BAR_CLOSE|TS_BAR_HISTORICAL_DATA|TS_BAR_END_OF_SESSION_CLOSE, ���� ��� ��������� � ������ 
//                   (0x10000015) TS_BAR_TECH_BAR|TS_BAR_OPEN|TS_BAR_HISTORICAL_DATA|TS_BAR_END_OF_SESSION_CLOSE, �����������(������) ���, ���� ��������� BAR != Session::Finish

//          REALTIME MODE: 
//               OPEN BAR: (0x03) TS_BAR_OPEN|TS_BAR_REALTIME_DATA 
//             INSIDE BAR: (0x02) TS_BAR_INSIDE|TS_BAR_REALTIME_DATA 
//              CLOSE BAR: (0x08) TS_BAR_CLOSE 
//                         (0x0A) TS_BAR_CLOSE|TS_BAR_REAL_TIME_DATA, ��� �������� N TickBar 
//                         (0x18) TS_BAR_CLOSE|TS_BAR_END_OF_SESSION_CLOSE, �������� �� �������� � ����� ������ 
//                         (0x1A) TS_BAR_REAL_TIME_DATA|TS_BAR_CLOSE|TS_BAR_END_OF_SESSION_CLOSE, �������� �� �������� � ����� ������ ��� VolumeBar
//                   (0x10000013) TS_BAR_TECH_BAR|TS_BAR_OPEN|TS_BAR_REAL_TIME_DATA|TS_BAR_END_OF_SESSION_CLOSE, �����������(������) ���, ���� ��������� BAR != Session::Finish
// --------------------------------------------------------------------------------------

enum tagTSBARSTATUS
{
  TS_BAR_NONE                   = 0x00000100,
  TS_BAR_OPEN                   = 0x00000001,
  TS_BAR_INSIDE                 = 0x00000000,
  TS_BAR_CLOSE                  = 0x00000008,
  TS_BAR_REAL_TIME_DATA         = 0x00000002,
  TS_BAR_HISTORICAL_DATA        = 0x00000004,

  // ����� �������
  TS_BAR_END_OF_SESSION_CLOSE   = 0x00000010,
  TS_BAR_UPDATE_CORRECTION      = 0x00000040, // ??????
  TS_BAR_GAP                    = 0x02000000, // �� 2000 �. ������������(��� ������), ����������� ���(�� �������� ������) 
  TS_BAR_REST_HISTORY           = 0x04000000, // ����� ������������ (������� ������� �� �����, �������� � ������, ��������� �� ������, � ������ �� ��������) 
  TS_BAR_TECH_BAR               = 0x10000000  // ����������� ���(�� �������� ������)
};

