# vouchertool
A generic telecommunication-grade voucher generation SOA webservice designed to generate and manage vouchers at scale

I designed and developed this service after seeing a vendor make millions of Euros selling airtime voucher generation and management 
software. This was also called "pin generation". I thought I could do a better job than the said vendor and so built vouchertool.

The service (SOAP based, contract-first design) is multi-user by definition, this means that a large enterprise could use the service 
to serve multiple distinct client applications, each with its own unique voucher generation pin space and style. 
