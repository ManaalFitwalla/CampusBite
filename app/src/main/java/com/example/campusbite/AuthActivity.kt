package com.example.campusbite

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.campusbite.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var selectedImageUri: Uri? = null
    private var isStaffSelected = false

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imgIdCardPreview.setImageURI(it)
            binding.imgIdCardPreview.visibility = View.VISIBLE
            binding.btnUploadIdCard.text = "ID Card Selected ✓"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDepartmentAndProgramSpinners()

        // Tab selection listener
        binding.btnTabStudent.setOnClickListener { selectStudentTab() }
        binding.btnTabStaff.setOnClickListener { selectStaffTab() }

        binding.tvSwitchToSignup.setOnClickListener { showSignupStep1() }
        binding.tvSwitchToLogin.setOnClickListener { showLogin() }

        // Initial tab styling setup
        selectStudentTab()

        // Action: Login
        binding.btnLogin.setOnClickListener {
            val loginId = binding.etLoginId.text.toString().trim()
            val pass = binding.etLoginPassword.text.toString().trim()

            if (isStaffSelected) {
                // Canteen Staff Validation
                if (loginId == "mhsspcanteen@gmail.com" && pass == "00000") {
                    Toast.makeText(this, "Welcome Canteen Staff!", Toast.LENGTH_SHORT).show()
                    saveStaffLoginStateAndProceed()
                } else {
                    Toast.makeText(this, "Invalid Canteen Staff credentials!", Toast.LENGTH_LONG).show()
                }
            } else {
                // Regular Student Validation
                if (loginId.isNotEmpty() && pass.isNotEmpty()) {
                    val nameFromEmail = loginId.substringBefore("@").replaceFirstChar { it.uppercase() }
                    saveLoginStateAndProceed(if (nameFromEmail.isNotEmpty()) nameFromEmail else "Student", "", "")
                } else {
                    Toast.makeText(this, "Please enter email/phone and password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Action: Send OTP
        binding.btnSendOtp.setOnClickListener {
            val contact = binding.etSignupContact.text.toString().trim()
            val pass = binding.etSignupPassword.text.toString().trim()

            if (contact.isNotEmpty() && pass.isNotEmpty()) {
                Toast.makeText(this, "OTP sent! Use 1234 to verify.", Toast.LENGTH_LONG).show()
                binding.containerSignupStep1.visibility = View.GONE
                binding.containerOtp.visibility = View.VISIBLE
                binding.tvHeaderSub.text = "OTP Verification"
            } else {
                Toast.makeText(this, "Fill contact and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Action: Verify OTP
        binding.btnVerifyOtp.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()
            if (otp == "1234") {
                Toast.makeText(this, "OTP Verified successfully!", Toast.LENGTH_SHORT).show()
                binding.containerOtp.visibility = View.GONE
                binding.containerStudentDetails.visibility = View.VISIBLE
                binding.tvHeaderSub.text = "Student Registration Details"
            } else {
                Toast.makeText(this, "Invalid OTP! Enter 1234", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUploadIdCard.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        // Action: Complete Signup
        binding.btnCompleteRegistration.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val enroll = binding.etEnrollmentNo.text.toString().trim()
            val roll = binding.etRollNo.text.toString().trim()

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Please enter your Full Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enroll.isEmpty() || roll.isEmpty()) {
                Toast.makeText(this, "Please enter Enrollment and Roll Number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enroll.toLongOrNull() == null || roll.toLongOrNull() == null) {
                Toast.makeText(this, "Enrollment and Roll Number must contain digits only", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Please upload your ID card image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Registration Complete!", Toast.LENGTH_SHORT).show()
            saveLoginStateAndProceed(fullName, roll, enroll)
        }
    }

    private fun selectStudentTab() {
        isStaffSelected = false
        // Dark Teal for active student tab
        binding.btnTabStudent.setBackgroundColor(Color.parseColor("#005B5C"))
        binding.btnTabStudent.setTextColor(Color.WHITE)

        // Soft Light Green for inactive staff tab with clear white text
        binding.btnTabStaff.setBackgroundColor(Color.parseColor("#A3D9A5"))
        binding.btnTabStaff.setTextColor(Color.WHITE)

        binding.tvHeaderSub.text = "Student Login"
        binding.tvLoginLabel.text = "Email or Phone Number"
        binding.etLoginId.hint = "e.g. student@college.edu / 9876543210"
        binding.tvSwitchToSignup.visibility = View.VISIBLE
        showLogin()
    }

    private fun selectStaffTab() {
        isStaffSelected = true
        // Dark Teal for active staff tab
        binding.btnTabStaff.setBackgroundColor(Color.parseColor("#005B5C"))
        binding.btnTabStaff.setTextColor(Color.WHITE)

        // Soft Light Green for inactive student tab with clear white text
        binding.btnTabStudent.setBackgroundColor(Color.parseColor("#A3D9A5"))
        binding.btnTabStudent.setTextColor(Color.WHITE)

        binding.tvHeaderSub.text = "Canteen Staff Login"
        binding.tvLoginLabel.text = "Staff Email Address"
        binding.etLoginId.hint = "mhsspcanteen@gmail.com"
        binding.tvSwitchToSignup.visibility = View.GONE
        showLogin()
    }

    private fun setupDepartmentAndProgramSpinners() {
        val programs = arrayOf("Diploma", "Degree")
        val programAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, programs)
        binding.spDegreeDiploma.adapter = programAdapter

        val departments = arrayOf(
            "Computer (aided)", "Computer (Unaided)", "Mechanical (Aided)",
            "Mechanical (Unaided)", "AI", "Civil (Aided)", "Civil (Unaided)",
            "Electrical (Aided)", "Electrical (Unaided)", "IT"
        )
        val deptAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, departments)
        binding.spDept.adapter = deptAdapter

        binding.spDegreeDiploma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedProgram = programs[position]
                val years = if (selectedProgram == "Diploma") {
                    arrayOf("1st Year", "2nd Year", "3rd Year")
                } else {
                    arrayOf("1st Year", "2nd Year", "3rd Year", "4th Year")
                }
                val yearAdapter = ArrayAdapter(this@AuthActivity, android.R.layout.simple_spinner_dropdown_item, years)
                binding.spYear.adapter = yearAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showLogin() {
        binding.containerLogin.visibility = View.VISIBLE
        binding.containerSignupStep1.visibility = View.GONE
        binding.containerOtp.visibility = View.GONE
        binding.containerStudentDetails.visibility = View.GONE
    }

    private fun showSignupStep1() {
        binding.containerLogin.visibility = View.GONE
        binding.containerSignupStep1.visibility = View.VISIBLE
        binding.containerOtp.visibility = View.GONE
        binding.containerStudentDetails.visibility = View.GONE
        binding.tvHeaderSub.text = "Create Student Account"
    }

    private fun saveLoginStateAndProceed(userName: String, rollNo: String = "", enrollmentNo: String = "") {
        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
            .putBoolean("isLoggedIn", true)
            .putBoolean("isStaff", false)
            .putString("userName", userName)

        if (rollNo.isNotEmpty()) editor.putString("rollNo", rollNo)
        if (enrollmentNo.isNotEmpty()) editor.putString("enrollmentNo", enrollmentNo)

        editor.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun saveStaffLoginStateAndProceed() {
        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        sharedPref.edit()
            .putBoolean("isLoggedIn", true)
            .putBoolean("isStaff", true)
            .putString("userName", "MHSSP Canteen Staff")
            .apply()

        val intent = Intent(this, VendorDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}