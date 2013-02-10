package com.lolbro.nian;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ShopActivity extends Activity implements OnClickListener {
	
	private final static int TESLA_PRICE = 50;
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefsEdit;
	
	private TextView textCredits;
	private ImageButton buttonTesla;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_layout);
		
		prefs = getSharedPreferences("nian_preferences", 0);
		prefsEdit = prefs.edit();
		
		textCredits = (TextView) findViewById(R.id.shop_text_credits);
		buttonTesla = (ImageButton) findViewById(R.id.shop_tesla);
		
		buttonTesla.setOnClickListener(this);
		
		refreshCreditsView();
	}
	
	private void refreshCreditsView() {
		int credits = prefs.getInt("coupons", 0);
		String stringNoCredits = getString(R.string.shop_credits);
		stringNoCredits = String.format(stringNoCredits, credits);
		textCredits.setText(stringNoCredits);
	}
	
	@Override
	public void onClick(View v) {
		int credits = prefs.getInt("coupons", 0);
		
		switch (v.getId()) {
		case R.id.shop_tesla:
			if(credits < TESLA_PRICE){
				Toast.makeText(this, R.string.shop_not_enough_credits, Toast.LENGTH_SHORT).show();
				return;
			}
			int currentTeslas = prefs.getInt("tesla_coils", 0);
			prefsEdit.putInt("tesla_coils", currentTeslas+1);
			prefsEdit.putInt("coupons", credits-TESLA_PRICE);
			prefsEdit.commit();
			refreshCreditsView();
			break;
		}
		
	}
	
}
