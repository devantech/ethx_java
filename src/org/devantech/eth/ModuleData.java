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

/**
 *
 * @author James Henderson
 */
public class ModuleData {
    
    /**
     * The ID of an ETH002
     */
    public static final int ETH002_ID = 18;

    /**
     *  The ID of an ETH008
     */
    public static final int ETH008_ID = 19;

    /**
     *  The ID of an ETH484
     */
    public static final int ETH484_ID = 20;

    /**
     *  The ID of an ETH8020
     */
    public static final int ETH8020_ID = 21;

    /**
     *  The ID of an ETH0621
     */
    public static final int ETH0621_ID = 23;

    /**
     *  The ID of an ETH044
     */
    public static final int ETH044_ID = 29;

    /**
     *  The ID of an ETH1620
     */
    public static final int ETH1620_ID = 51;

    /**
     *  The ID of an ETH1610
     */
    public static final int ETH1610_ID = 52;
    
    /**
     * The ID of an ETH24V008
     */
    private static final int ETH24V008_ID = 54;
    
    /**
     * The module ID
     */
    public final int id;

    /**
     * The number of analogue inputs this module has
     */
    public final int analogue_input_count;

    /**
     *  The number of bytes returned by the get digital input commands
     */
    public final int digital_input_byte_count;

    /**
     *  The number of bytes returned by the get digital outputs command
     */
    public final int digital_output_byte_count;

    /**
     *  The number of analogue outputs this module has
     */
    public final int analogue_output_count;

    /**
     *  The name of the module
     */
    public final String module_name;
    
    /**
     *  Constructor that creates an instance of a modules data.
     * @param module_id The Module ID.
     */
    public ModuleData(int module_id) {
        id = module_id;
        module_name = getModuleName();
        digital_input_byte_count = digitalInByteCount();
        digital_output_byte_count = digitalOutByteCount();
        analogue_input_count = analogueInputCount();
        analogue_output_count = analogueOutputCount();
    }
    
    /**
     * Returns the name of the connected module.
     * 
     * @return string containing the module name.
     */
    private String getModuleName() {
        switch (id) {
            default:
                return "none";
            case ETH002_ID:
                return "ETH002";
            case ETH008_ID:
                return "ETH008";
            case ETH484_ID:
                return "ETH484";
            case ETH8020_ID:
                return "ETH8020";
            case ETH0621_ID:
                return "ETH0621";
            case ETH044_ID:
                return "ETH044";
            case ETH1620_ID:
                return "ETH1620";
            case ETH1610_ID:
                return "ETH1610";
            case ETH24V008_ID:
                return "ETH24V008";
                
        }
    }
    
    /**
     * Get the number of bytes the get digital outputs command will return from the module.
     * @return 
     */
    private int digitalOutByteCount() {
        
        switch (id) {
            default:
                return 0;
            case ETH002_ID:
                return 1;
            case ETH008_ID:
                return 1;
            case ETH484_ID:
                return 2;
            case ETH8020_ID:
                return 3;
            case ETH0621_ID:
                return 3;
            case ETH044_ID:
                return 2;
            case ETH1620_ID:
                return 3;
            case ETH1610_ID:
                return 2;
            case ETH24V008_ID:
                return 1;
        }
    }
    
    /**
     * Get the number of bytes the get digital inputs command will return from the module.
     * @return 
     */
    private int digitalInByteCount() {
        
        switch (id) {
            default:
                return 0;
            case ETH484_ID:
                return 2;
            case ETH8020_ID:
                return 4;
            case ETH0621_ID:
                return 4;
            case ETH044_ID:
                return 2;
            case ETH24V008_ID:
                return 1;
        }
    }
    
        /**
     * Get the number of analogue inputs that the module has.
     * @return 
     */
    private int analogueInputCount() {
        
        switch (id) {
            default:
                return 0;
            case ETH484_ID:
                return 4;
            case ETH8020_ID:
                return 8;
            case ETH0621_ID:
                return 1;
            case ETH1620_ID:
                return 16;
            case ETH1610_ID:
                return 16;
        }
        
    }
    
    /**
     * The number of analogue outputs that the module has.
     * @return the number of outputs.
     */
    private int analogueOutputCount() {
        
        switch (id) {
            default:
                return 0;
            case ETH044_ID:
                return 4;
            case ETH0621_ID:
                return 2;
        }
    
    }
    
}
