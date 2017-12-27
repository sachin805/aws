package p1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.kite.aws.login.Login;
import com.rainmatter.kiteconnect.KiteConnect;

public class DataDemo {

	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter token :");
		String token = br.readLine().trim();
		Object[] reply = Login.login(token);
		String code = (String) reply[0];
		
		if (code.equals("success")) {
			System.out.println("Login success");
			KiteConnect kiteconnect = (KiteConnect) reply[2];
		}

	}

}
