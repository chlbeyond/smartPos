package com.rainbow.smartpos.check;

import java.util.Date;

public class CheckPaymentCash {
		private int bill_id;
		private String payment_type;
		private double value;
		private int staff_id;
		private java.util.Date pay_time;
		private String remark;

		public int getBill_id(){
			return bill_id;
		}

		public void setBill_id(int bill_id){
			this.bill_id=bill_id;
		}

		public String getPayment_type(){
			return payment_type;
		}

		public void setPayment_type(String payment_type){
			this.payment_type=payment_type;
		}

		public double getValue(){
			return value;
		}

		public void setValue(double value){
			this.value=value;
		}

		public int getStaff_id(){
			return staff_id;
		}

		public void setStaff_id(int staff_id){
			this.staff_id=staff_id;
		}

		public java.util.Date getPay_time(){
			return pay_time;
		}

		public void setPay_time(Date pay_time){
			this.pay_time=pay_time;
		}

		public String getRemark(){
			return remark;
		}

		public void setRemark(String remark){
			this.remark=remark;
		}
}
