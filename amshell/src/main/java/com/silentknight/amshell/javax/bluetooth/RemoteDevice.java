/*
 * Copyright 2012 Kulikov Dmitriy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.silentknight.amshell.javax.bluetooth;

import android.bluetooth.BluetoothDevice;

public class RemoteDevice
{
	protected BluetoothDevice device;
	
	public RemoteDevice(BluetoothDevice device)
	{
		this.device = device;
	}
	
	public String getBluetoothAddress()
	{
		return BTUtils.formatBluetoothAddress(device.getAddress());
	}
	
	public String getFriendlyName(boolean alwaysAsk)
	{
		return device.getName();
	}
}