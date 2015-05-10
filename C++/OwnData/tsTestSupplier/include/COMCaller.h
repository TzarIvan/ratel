#ifndef __COMCALLER__
#define __COMCALLER__

#pragma once 

#include <wtypes.h>
// ��������� �������� methods COM �������

inline __declspec(naked)
HRESULT ComCall(int MethodNum,void* Object, void *args,int ArgSize)
{
	_asm{
		push ebp
		mov	ebp, esp
		push ecx
		push edx

		mov ecx, ArgSize
		test ecx, ecx	// ���� ������ ���������� 0, �� 
		jz IsVoid		// �� ��������

		mov edx, args
	
		sub esp, ArgSize	// ����������� ����� ��� ����������
											
m1:
		mov al, byte ptr [edx + ecx - 1]	// �������� ���������
		mov byte ptr [esp + ecx - 1], al	// 
		loop m1
IsVoid:		
		mov eax, MethodNum
		mov	ecx, Object						// ����� �������
		mov edx, dword ptr [ ecx ]			// ����� ����. �������

		
		
		push ecx							// ���������� this
		call [edx + eax * 4]				// ����� ������


		pop edx
		pop ecx
		pop ebp
		
		ret
	}
}

#endif