# FBIT
"FedEx Barcode Identification Tool"

A tool designed to assist in rectifying unreadable barcodes with up to 3 missing tracking IDs.

The known digits are entered, and an asterisk is put in place of the unknown values. A brute force method is used to generate all possible combinations of the tracking ID (10^n)

The tracking IDs are then fed into the tracking application, using selenium webdriver, which assists in the next step of filtering out invalid numbers. 
The result is a page opened in the tracking application with all possible valid barcodes, which can then be analyzed further to make the determination of which one matches the damaged barcode.

NOTE: The link to the tracking application in the source code has been removed for sake of privacy
