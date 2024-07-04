package Service;
import Model.Account;
import DAO.AccountDAO;

import java.util.ArrayList;
import java.util.List;

public class AccountService {
    private AccountDAO accountDAO = new AccountDAO();
    
    public Account addAccount(Account account){
        return accountDAO.insertNewAccount(account);
    }
    public Account isAccountExist(Account account){
        List<Account> accounts = new ArrayList<>();
        accounts = accountDAO.getAllAccounts();
        String name = account.getUsername();
        String pass = account.getPassword();
        for(Account a: accounts){
            if(a.getUsername() == name && a.getPassword() == pass)
                return a;
        }
        return null;
    }
}
