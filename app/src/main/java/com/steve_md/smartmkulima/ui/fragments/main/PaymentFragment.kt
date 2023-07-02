package com.steve_md.smartmkulima.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.steve_md.smartmkulima.R
import com.steve_md.smartmkulima.data.remote.DarajaApiClient
import com.steve_md.smartmkulima.databinding.FragmentPaymentBinding
import com.steve_md.smartmkulima.payment.mpesa.dto.AuthorizationResponse
import com.steve_md.smartmkulima.payment.mpesa.dto.StkPushRequest
import com.steve_md.smartmkulima.payment.mpesa.dto.StkPushSuccessResponse
import com.steve_md.smartmkulima.utils.Constants
import com.steve_md.smartmkulima.utils.Constants.BUSINESS_SHORT_CODE
import com.steve_md.smartmkulima.utils.Constants.CALLBACKURL
import com.steve_md.smartmkulima.utils.Constants.PARTYB
import com.steve_md.smartmkulima.utils.Constants.PASSKEY
import com.steve_md.smartmkulima.utils.Constants.SANDBOX_BASE_URL
import com.steve_md.smartmkulima.utils.RegEx
import com.steve_md.smartmkulima.utils.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

class PaymentFragment : Fragment() ,View.OnClickListener{


    private var mApiClient: DarajaApiClient? = null

    var mAmount: EditText? = null
    var mPhone: EditText? = null
    var mPay: Button? = null

    private lateinit var binding:FragmentPaymentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        mAmount= view.findViewById(R.id.inputAmountToPay)
        mPhone = view.findViewById(R.id.inputPhoneNumber)
        mPay = view.findViewById(R.id.pay)

        // TODO hide these Keys
        val consumerKey = "FG8ad21FyJDGWALcyur1437M7sZ0kQuR"
        val consumerSecret = "3rRqAkJcajHxzeur"

        mApiClient = DarajaApiClient(
            consumerKey,
            consumerSecret,
            SANDBOX_BASE_URL
        )

        mApiClient!!.setIsDebug(true)
        mPay!!.setOnClickListener(this)

        getAccessToken()
    }

    private fun getAccessToken() {
        mApiClient!!.setGetAccessToken(true)
        mApiClient!!.mpesaService()!!.getAccessToken().enqueue(object :
            Callback<AuthorizationResponse?> {
            override fun onResponse(call: Call<AuthorizationResponse?>, response: Response<AuthorizationResponse?>) {
                if (response.isSuccessful) {
                    mApiClient!!.setAuthToken(response.body()?.accessToken)
                }
            }

            override fun onFailure(call: Call<AuthorizationResponse?>, t: Throwable) {
                Timber.tag("MainActivity").e(t.printStackTrace().toString())
            }
        })
    }

    override fun onClick(v: View?) {
        if (v === mPay ) {

            val phoneNumber = mPhone!!.text.toString()
            val amount = mAmount!!.text.toString()
            performSTKPush(phoneNumber, amount)
        }
    }

    private fun performSTKPush(phoneNumber: String, amount: String) {
        val timestamp = RegEx.getTimestamp()

        val stkPush = StkPushRequest(
            businessShortCode = BUSINESS_SHORT_CODE,
            password = RegEx.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp!!)!!,
            timestamp = timestamp,
            transactionType = Constants.TransactionType.CustomerPayBillOnline,
            amount = amount,
            partyA = RegEx.sanitizePhoneNumber(phoneNumber)!! ,
            partyB = PARTYB,
            phoneNumber = RegEx.sanitizePhoneNumber(phoneNumber)!!,
            callBackURL = CALLBACKURL,
            accountReference = "LIPA NA MPESA",
            transactionDesc = "LIPA NA MPESA C2B"
        )

        mApiClient!!.setGetAccessToken(false)

        mApiClient!!.mpesaService()!!.sendPush(stkPush).enqueue(object : Callback<StkPushSuccessResponse> {
            override fun onResponse(call: Call<StkPushSuccessResponse>, response: Response<StkPushSuccessResponse>) {

                try {
                    if (response.isSuccessful) {
                        toast("Response : ${response.body().toString()}")

                        navigateToDeliveryScreen()

                        Timber.tag("Post submitted to the API")
                    } else {
                        Timber.tag("Response %s")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<StkPushSuccessResponse>, t: Throwable) {
                Timber.tag(TAG).e(httpException, t.printStackTrace().toString())

            }
        })
    }

    private fun navigateToDeliveryScreen() {
        findNavController().navigate(R.id.action_paymentFragment_to_successfulPaymentFragment)
    }

    companion object {
        val httpException : HttpException? = null
        const val TAG = "MpesaPaymentFragment"
    }
}