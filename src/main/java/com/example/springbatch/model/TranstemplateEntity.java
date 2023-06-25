package com.example.springbatch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collation = "tanstemplate")
@Data
public class TranstemplateEntity {
    @Id
    private String transactionTemplateId;   //ID bảng TRANSACTION_TEMPLATE
    @Field("cust_id_sender")
    private String customerIdSender;       //Id KH chuyển tiền
    @Field("msisdn_sender")
    private String msisdnSender;            //SĐT người chuyển
    @Field("msisdn_receiver")
    private String msisdnReceiver;          //SĐT người nhận
    @Field("alias_receiver_name")
    private String aliasReceiverName;       //Tên gợi nhớ khách hàng
    @Field("to_account")
    private String toAccount;               //Số TK nhận tiền
    @Field("transfer_content")
    private String transferContent;         //Nội dung giao dịch
    @Field("viettel_bank_code_receiver")
    private String viettelBankCodeReceiver; //Mã ngân hàng nhận
    @Field("viettel_bank_code_sender")
    private String viettelBankCodeSender;   //Mã ngân hàng chuyển
    @Field("account_name")
    private String accountName;             //Tên người nhận
    @Field("transfer_amount")
    private Double transferAmount;          //Số tiền chuyển khoản
    @Field("trans_type_code")
    private String transTypeCode;           //Mã loại giao dịch: CT SĐT/ STK/ST
    @Field("pin_flg")
    private Integer pinFlg;                 //Check pin/unpin
    @Field("pin_time")
    private Date pinTime;                   //Ngày tạo
    @Field("delete_flg")
    private Integer deleteFlg;              //Trạng thái xóa
    @Field("create_date")
    private Date createDate;                //Ngày tạo
    @Field("create_by")
    private String createBy;                //Người tạo
    @Field("update_date")
    private Date updateDate;                //Ngày cập nhật
    @Field("update_by")
    private String updateBy;                //Người cập nhật
    @Field("source_infra_id")
    private String sourceInfraId;                //Người cập nhật
    @Field("cust_id_mobile")
    private String customerIdMobile;


}
