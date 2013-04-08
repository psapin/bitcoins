bitcoins
========

Bitcoin Arbitrage GUI. (Java)

Gets data from Mt. Gox and Virtex exchanges.

Code is old and needs to be revised, recommented, and reworked for interfacing with new Mt Gox API.

(Warning everything contained here was written while in high school and is still in an unprofessional format)


_______________
Troubleshooting:

If you get this error:

Error connecting: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

For Windows 7 x64 (other Windows is similar), add the included jssecacerts file to C:/Program Files/Java/jre7/lib/security/

Other operating systems: same jazz, add that file to your jre/lib/security folder.

You can also make your own jssecacerts file, but this is much easier.
