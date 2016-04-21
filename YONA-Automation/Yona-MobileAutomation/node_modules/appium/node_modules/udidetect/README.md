UDID detection utility
----------------------
Prints a UDID of a currently connected iOS device.  

### Example usage

Use it with instruments CLI:

```
instruments -w `udidetect` -t ...
```

Or use it to grab UDIDs off multiple devices for your Zucchini configuration:

```
udidetect -z
``` 