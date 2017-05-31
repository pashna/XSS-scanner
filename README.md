# XSS-scanner
XSS-Scanner is a **multi-threading** app that works in parallel in several browser windows to save time and improve efficiency.

It **emitates** client's activities by walking throught all the links on the web-site, filling all the forms and checking their safety.

After working, it creates a nice web page with a report of a test result.

This app is absolutely free XSS Scanner, based on Selenium Web Driver. It scans directly in your browser. All you need is FireFox with FireFoxDriver (usually it is built-in). 

To check vulnerabilities, XSS-scanner uses a list of known XSS-injection provided by owasp:
https://www.owasp.org/index.php/XSS_Filter_Evasion_Cheat_Sheet 
