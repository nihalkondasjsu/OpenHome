package com.openhome.data.manager;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.openhome.dao.TransactionDAO;
import com.openhome.data.Reservation;
import com.openhome.data.Transaction;
import com.openhome.data.Transaction.TransactionNature;
import com.openhome.data.Transaction.TransactionUser;

public class TransactionManager {

	@Autowired
	private TransactionDAO transactionDao;
	
	public void createTransaction(Date createdDate,
			Date dayToChargeFor,
			Double amount,
			Reservation reservation,
			TransactionNature transactionNature,
			TransactionUser transactionUser) {
		Transaction t1 = new Transaction(amount, createdDate, dayToChargeFor, reservation, transactionNature, transactionUser);
		if(transactionNature == TransactionNature.Charge || transactionNature == TransactionNature.Fee) {
			transactionNature = TransactionNature.Payment;
		}
		if(transactionUser == TransactionUser.Guest) {	
			transactionUser = TransactionUser.Host;
		}else if(transactionUser == TransactionUser.Host) {	
			transactionUser = TransactionUser.Guest;
		}
		Transaction t2 = new Transaction(amount, createdDate, dayToChargeFor, reservation, transactionNature, transactionUser);
		transactionDao.save(t1);
		transactionDao.save(t2);
	}
	
}
