package com.vouchertool.vouchserv.definitions;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.7.0
 * 2012-11-10T21:56:39.438+13:00
 * Generated source version: 2.7.0
 * 
 */
@WebService(targetNamespace = "http://vouchertool.com/vouchserv/definitions", name = "VoucherService")
@XmlSeeAlso({com.vouchertool.vouchserv.schemas.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface VoucherService {

    @WebResult(name = "VoucherBatchGenerationResponse", targetNamespace = "http://vouchertool.com/vouchserv/schemas", partName = "VoucherBatchGenerationResponse")
    @WebMethod(operationName = "VoucherBatchGeneration")
    public com.vouchertool.vouchserv.schemas.VoucherBatchGenerationResponse voucherBatchGeneration(
        @WebParam(partName = "VoucherBatchGenerationRequest", name = "VoucherBatchGenerationRequest", targetNamespace = "http://vouchertool.com/vouchserv/schemas")
        com.vouchertool.vouchserv.schemas.VoucherBatchGenerationRequest voucherBatchGenerationRequest
    );

    @WebResult(name = "GetVoucherResponse", targetNamespace = "http://vouchertool.com/vouchserv/schemas", partName = "GetVoucherResponse")
    @WebMethod(operationName = "GetVoucher")
    public com.vouchertool.vouchserv.schemas.GetVoucherResponse getVoucher(
        @WebParam(partName = "GetVoucherRequest", name = "GetVoucherRequest", targetNamespace = "http://vouchertool.com/vouchserv/schemas")
        com.vouchertool.vouchserv.schemas.GetVoucherRequest getVoucherRequest
    );

    @WebResult(name = "RegisterResponse", targetNamespace = "http://vouchertool.com/vouchserv/schemas", partName = "RegisterResponse")
    @WebMethod(operationName = "Register")
    public com.vouchertool.vouchserv.schemas.RegisterResponse register(
        @WebParam(partName = "RegisterRequest", name = "RegisterRequest", targetNamespace = "http://vouchertool.com/vouchserv/schemas")
        com.vouchertool.vouchserv.schemas.RegisterRequest registerRequest
    );

    @WebResult(name = "RedeemVoucherResponse", targetNamespace = "http://vouchertool.com/vouchserv/schemas", partName = "RedeemVoucherResponse")
    @WebMethod(operationName = "RedeemVoucher")
    public com.vouchertool.vouchserv.schemas.RedeemVoucherResponse redeemVoucher(
        @WebParam(partName = "RedeemVoucherRequest", name = "RedeemVoucherRequest", targetNamespace = "http://vouchertool.com/vouchserv/schemas")
        com.vouchertool.vouchserv.schemas.RedeemVoucherRequest redeemVoucherRequest
    );

    @WebResult(name = "ActivateVoucherBatchResponse", targetNamespace = "http://vouchertool.com/vouchserv/schemas", partName = "ActivateVoucherBatchResponse")
    @WebMethod(operationName = "ActivateVoucherBatch")
    public com.vouchertool.vouchserv.schemas.ActivateVoucherBatchResponse activateVoucherBatch(
        @WebParam(partName = "ActivateVoucherBatchRequest", name = "ActivateVoucherBatchRequest", targetNamespace = "http://vouchertool.com/vouchserv/schemas")
        com.vouchertool.vouchserv.schemas.ActivateVoucherBatchRequest activateVoucherBatchRequest
    );

    @WebResult(name = "DeactivateVoucherBatchResponse", targetNamespace = "http://vouchertool.com/vouchserv/schemas", partName = "DeactivateVoucherBatchResponse")
    @WebMethod(operationName = "DeactivateVoucherBatch")
    public com.vouchertool.vouchserv.schemas.DeactivateVoucherBatchResponse deactivateVoucherBatch(
        @WebParam(partName = "DeactivateVoucherBatchRequest", name = "DeactivateVoucherBatchRequest", targetNamespace = "http://vouchertool.com/vouchserv/schemas")
        com.vouchertool.vouchserv.schemas.DeactivateVoucherBatchRequest deactivateVoucherBatchRequest
    );

    @WebResult(name = "ActivateVoucherSerialRangeResponse", targetNamespace = "http://vouchertool.com/vouchserv/schemas", partName = "ActivateVoucherSerialRangeResponse")
    @WebMethod(operationName = "ActivateVoucherSerialRange")
    public com.vouchertool.vouchserv.schemas.ActivateVoucherSerialRangeResponse activateVoucherSerialRange(
        @WebParam(partName = "ActivateVoucherSerialRangeRequest", name = "ActivateVoucherSerialRangeRequest", targetNamespace = "http://vouchertool.com/vouchserv/schemas")
        com.vouchertool.vouchserv.schemas.ActivateVoucherSerialRangeRequest activateVoucherSerialRangeRequest
    );

    @WebResult(name = "GetVoucherBatchResponse", targetNamespace = "http://vouchertool.com/vouchserv/schemas", partName = "GetVoucherBatchResponse")
    @WebMethod(operationName = "GetVoucherBatch")
    public com.vouchertool.vouchserv.schemas.GetVoucherBatchResponse getVoucherBatch(
        @WebParam(partName = "GetVoucherBatchRequest", name = "GetVoucherBatchRequest", targetNamespace = "http://vouchertool.com/vouchserv/schemas")
        com.vouchertool.vouchserv.schemas.GetVoucherBatchRequest getVoucherBatchRequest
    );
}
