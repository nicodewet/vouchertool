# vouchertool
A generic telecommunication-grade voucher generation SOA webservice designed to generate and manage vouchers at scale

I designed and developed this service over 3 years after seeing a vendor make millions of Euros selling airtime voucher generation 
and management software. This was also called "pin generation". I thought I could do a better job than the said vendor and so built 
vouchertool.

The service (SOAP based, contract-first design) is multi-user by definition, this means that a large enterprise could use the service 
to serve multiple distinct client applications, each with its own unique voucher generation pin space and style.

Due to the modular nature of how it has been built, in the Java space one could simply re-use the core module, vouchserv.

Vouchertool is composed of four modules and two run-time components:

 * [run-time component] vouchertool - web application with registration and management functionality
 * [run-time component] vouchserv-ws - SOAP based web service
 * [module] vouchserv-api - 
 * [module] vouchserv -

Note, I originally hosted vouchertool on www.vouchertool.com and developed it under the pseudo company name MAYLOOM. That said the 
source is now here under the Apache 2.0 licence.   
