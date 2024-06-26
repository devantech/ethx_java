/*
 * Copyright(C) 2023 Devantech Ltd <support@robot-electronics.co.uk>
 * 
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or
 * without fee is hereby granted, provided that the above copyright notice and
 * this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO
 * THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 * DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN
 * AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package org.devantech.eth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a connection to an ETH module from Devantech.
 *
 * @author James Henderson
 */
public class ETHModule {

    private static final byte GET_MODULE_INFO = 0x10;
    private static final byte GET_DIGI_OUTPUT = 0x24;
    private static final byte GET_DIGI_INPUT = 0x25;
    private static final byte GET_ANALOGUE_INPUT = 0x32;
    private static final byte GET_ANALOGUE_INPUT_12_BIT = 0x33;
    private static final byte SET_ANALOGUE_VOLTAGE = 0x30;
    private static final byte DIGITAL_OUTPUT_ACTIVE = 0x20;
    private static final byte DIGITAL_OUTPUT_INACTIVE = 0x21;
    private static final byte GET_SERIAL_NUMBER = 0x77;
    private static final byte GET_PSU = 0x78;
    private static final byte LOGOUT = 0x7B;
    private static final byte SET_PASSWORD = 0x79;
    private static final byte GET_UNLOCK = 0x7a;

    private Socket socket = null;
    private OutputStream output = null;
    private InputStream input = null;

    private int id = 0;
    private int hardware = 0;
    private int firmware = 0;

    private final ModuleData mod;
    
    /**
     * Create a connection to a module that is password protected.
     * 
     * @param ip the IP of the module
     * @param port the port to talk on
     * @param password the password to send
     * @throws IOException an error while trying to communicate with the module
     */
    public ETHModule(String ip, int port, String password) throws IOException {
        socket = new Socket(ip, port);
        output = socket.getOutputStream();
        input = socket.getInputStream();

        sendPassword(password);
        
        getModuleData();
        mod = new ModuleData(id);
    }

    /**
     * Create a connection to a module that is not password protected.
     * 
     * @param ip the IP of the module
     * @param port the port to talk on
     * @throws IOException an error while trying to communicate with the module
     */
    public ETHModule(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        output = socket.getOutputStream();
        input = socket.getInputStream();
        getModuleData();
        mod = new ModuleData(id);
    }

    /**
     * Close the connection to the module
     */
    public void close() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
                input = null;
                output = null;
            } catch (IOException ex) {
                // Nothing to do here, the module was either already closed or was unable to close properly.
            }
        }
    }

    /**
     * Get the module information such as the ID and the firmware version.
     */
    public final void getModuleData() {

        byte[] data = new byte[127];
        data[0] = GET_MODULE_INFO;

        try {
            output.write(data, 0, 1);
            input.read(data, 0, 3);
            id = data[0];
            hardware = data[1];
            firmware = data[2];
        } catch (IOException ex) {
            id = 0;
            hardware = 0;
            firmware = 0;
        }

    }

    /**
     * Returns the ID of the module connected to. An ID of 0 means that no valid
     * module is connected.
     *
     * @return the module ID.
     */
    public int getID() {
        return id;
    }

    /**
     * Returns the firmware version of the connected module. A version of 0
     * means that no valid module is connected.
     *
     * @return the firmware version.
     */
    public int getFirmwareVersion() {
        return firmware;
    }

    /**
     * Returns the hardware version of the connected module. A value of 0 means
     * no valid module is connected;
     *
     * @return the hardware version.
     */
    public int getHardwareVersion() {
        return hardware;
    }

    /**
     * Get the module name associated with the module ID.
     *
     * @return the module name as a string.
     */
    public String getModuleName() {
        return mod.module_name;
    }

    /**
     * Get the unique 6 byte mac address from the module.
     *
     * @return a byte array containing the serial number.
     */
    public byte[] getSerialNumber() {
        byte[] data = new byte[127];
        data[0] = GET_SERIAL_NUMBER;

        try {
            output.write(data, 0, 1);
            input.read(data, 0, 6);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return Arrays.copyOfRange(data, 0, 6);

    }

    /**
     * Get the power supply voltage from the module.
     *
     * @return -1 for error otherwise a single byte representing the power
     * supply voltage.
     */
    public byte getPSU() {
        byte[] data = new byte[127];
        data[0] = GET_PSU;

        try {
            output.write(data, 0, 1);
            input.read(data, 0, 1);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return data[0];

    }

    /**
     * Logout from the module.
     *
     * @return -1 on failure 0 for success.
     */
    public byte logout() {
        byte[] data = new byte[127];
        data[0] = LOGOUT;

        try {
            output.write(data, 0, 1);
            input.read(data, 0, 1);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return data[0];
    }

    /**
     * Get the unlock time from the module.
     *
     * @return the unlock time.
     */
    public byte getUnlock() {
        byte[] data = new byte[127];
        data[0] = GET_UNLOCK;

        try {
            output.write(data, 0, 1);
            input.read(data, 0, 1);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return data[0];
    }

    /**
     * Send the password to the module.
     *
     * @param pass the password to send.
     * @return -1 for error, otherwise 1 for success and 2 for failure.
     */
    public final byte sendPassword(String pass) {
        byte[] data = new byte[127];
        data[0] = SET_PASSWORD;
        
        int index;
        for (index = 0; index < pass.length(); index++) {
            data[1 + index] = (byte) (pass.charAt(index) & 0xff);
        }

        try {
            output.write(data, 0, pass.length() + 1);
            input.read(data, 0, 1);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return data[0];

    }

    /**
     * Get the state of the outputs from the module as an array of bytes. Refer
     * to your modules manual for details of how the outputs are represented.
     *
     * @return a byte array with the output states in, or null if an error
     * occurred.
     */
    public byte[] getDigitalOutputStates() {

        if (mod.digital_output_byte_count == 0) {
            return null;
        }

        byte[] data = new byte[127];
        data[0] = GET_DIGI_OUTPUT;

        try {
            output.write(data, 0, 1);
            input.read(data, 0, mod.digital_output_byte_count);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return Arrays.copyOfRange(data, 0, mod.digital_output_byte_count);

    }

    /**ames
     * Get the state of the outputs from the module as an array of bytes. Refer
     * to your modules manual for details of how the outputs are represented.
     *
     * @return a byte array with the output states in, or null if an error
     * occurred.
     */
    public byte[] getDigitalInputStates() {

        if (mod.digital_input_byte_count == 0) {
            return null;
        }

        byte[] data = new byte[127];
        data[0] = GET_DIGI_INPUT;

        try {
            output.write(data, 0, 1);
            input.read(data, 0, mod.digital_input_byte_count);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return Arrays.copyOfRange(data, 0, mod.digital_input_byte_count);

    }

    /**
     * Get the state of an analogue input from the module.Refer to your modules
     * manual for what inputs and ranges are available.
     *
     * @param channel the analogue channel to read.
     * @return a byte array with the output states in, or null if an error
     * occurred.
     */
    public byte[] getAnalogueVoltage(int channel) {

        if (mod.analogue_input_count == 0) {
            return null;
        }
        if (channel > mod.analogue_input_count) {
            return null;
        }

        byte[] data = new byte[127];
        data[0] = GET_ANALOGUE_INPUT;
        data[1] = (byte) (channel & 0xff);

        try {
            output.write(data, 0, 2);
            input.read(data, 0, 2);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return Arrays.copyOfRange(data, 0, 2);

    }

    /**
     * Get the state of a 12 bit analogue input from the module.Refer to your 
     * modules manual for what inputs and ranges are available.
     * 
     * The 12 bit analogue read is only available on the ETH484b module at time of writing.
     *
     * @param channel the analogue channel to read.
     * @return a byte array with the output states in, or null if an error
     * occurred.
     */
    public byte[] getAnalogueVoltage12Bit(int channel) {

        if (mod.analogue_input_count == 0) {
            return null;
        }
        
        if (channel > mod.analogue_input_count) {
            return null;
        }

        byte[] data = new byte[127];
        data[0] = GET_ANALOGUE_INPUT_12_BIT;
        data[1] = (byte) (channel & 0xff);

        try {
            output.write(data, 0, 2);
            input.read(data, 0, 2);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return Arrays.copyOfRange(data, 0, 2);

    }
    
    /**
     * Get the analogue input as an integer.
     * 
     * @param channel the channel to read
     * @return and int representing the analogue input value.
     */
    public int getAnalogueVoltageInt(int channel) {
        byte[] vts = getAnalogueVoltage(channel);
        int out = vts[0] & 0xff;
        out <<= 8;
        out |= vts[1] & 0xff;
        return out;
    }
    
    /**
     * Get the 12 bit analogue input as an integer.
     * 
     * The 12 bit analogue read is only available on the ETH484b module at time of writing.
     * 
     * @param channel the channel to read
     * @return and int representing the analogue input value.
     */
    public int getAnalogueVoltage12BitInt(int channel) {
        byte[] vts = getAnalogueVoltage12Bit(channel);
        int out = vts[0] & 0xff;
        out <<= 8;
        out |= vts[1] & 0xff;
        return out;
    }

    /**
     * Make a digital output on the module active. Refer to your modules manual
     * for what outputs are available.
     *
     * @param channel the output to set active
     * @param time the length of time to set the output active
     * @return 1 for success, 0 for failure and -1 for an error.
     */
    public int digitalOutputActive(int channel, int time) {

        byte[] data = new byte[127];
        data[0] = DIGITAL_OUTPUT_ACTIVE;
        data[1] = (byte) (channel & 0xff);
        data[2] = (byte) (time & 0xff);

        try {
            output.write(data, 0, 3);
            input.read(data, 0, 1);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return data[0];

    }

    /**
     * Make a digital output on the module inactive. Refer to your modules
     * manual for what outputs are available.
     *
     * @param channel the output to set inactive
     * @param time the length of time to set the output inactive
     * @return 1 for success, 0 for failure and -1 for an error.
     */
    public int digitalOutputInctive(int channel, int time) {

        byte[] data = new byte[127];
        data[0] = DIGITAL_OUTPUT_INACTIVE;
        data[1] = (byte) (channel & 0xff);
        data[2] = (byte) (time & 0xff);

        try {
            output.write(data, 0, 3);
            input.read(data, 0, 1);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return data[0];

    }

    /**
     * Sets the value of an analogue output on the module. Refer to your modules
     * manual for details of what outputs and ranges are available.
     *
     * @param channel the output channel to set
     * @param value the value to set it to
     * @param time the time that you want the output to be set for.
     *
     * @return 1 for success, 0 for failure and -1 for error.
     */
    public int setAnalogueVoltage(int channel, int value, int time) {

        if (mod.analogue_output_count == 0) {
            return -1;
        }
        if (channel > mod.analogue_output_count) {
            return -1;
        }

        byte[] data = new byte[127];
        data[0] = SET_ANALOGUE_VOLTAGE;
        data[1] = (byte) (channel & 0xff);
        data[2] = (byte) (value & 0xff);
        data[3] = (byte) (time & 0xff);

        try {
            output.write(data, 0, 4);
            input.read(data, 0, 1);
        } catch (IOException ex) {
            Logger.getLogger(ETHModule.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return data[0];

    }

}
