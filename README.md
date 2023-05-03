# ETHx Java Library

A Java library for controlling the Devantech ETH range of modules.

The library is distributed as a jar file. Add the jar file to your projects class path to use it. 
The Javdoc is also distributed as a jar file.

# Examples

## Finding modules on your local network

The following code snippet shows how to discover any ETH modules on your local network.

```
class ScanExample implements ETHScanDelegate {

    ETHScan s = new ETHScan();
    
    public ScanExample() {
        startScan();
    }
    
    final void startScan() {
        s.addDelegate(this);
        s.udpAction();
    }
    
    @Override
    public void moduleFound(ETHScan.ScanResult module) {
        System.out.println(module.host_name + " " + module.ip + " " + module.id);
    }
}
```

## Toggling an output.

The following code snippet shows a simple example of how to connect to a module and toggle the state of digital output 1.

```
// Create an instance of an ETHModule.
ETHModule mod = null;

try {
    // Try and connect to the module.
    mod = new ETHModule("192.168.0.228", 17494, "password");
    System.out.println("Connected to : " + mod.getModuleName());
} catch (IOException ex) {
    Logger.getLogger(ETHTest.class.getName()).log(Level.SEVERE, null, ex);
    return;
}

// Get the state of the digital outputs from the module.
byte[] outputs = mod.getDigitalOutputStates();

// Check the state of output 1 and toggle it active or inactive.
if ( (outputs[0] & 0x01) == 1 ) {
    mod.digitalOutputInctive(1, 0);
} else {
    mod.digitalOutputActive(1, 0);
}

mod.close();
```

