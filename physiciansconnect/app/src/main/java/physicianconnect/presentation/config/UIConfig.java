package physicianconnect.presentation.config;

import java.time.format.DateTimeFormatter;

public final class UIConfig {
    // ─────────── Button & Label Text ───────────
    public static final String LOGIN_BUTTON_TEXT = "Login";
    public static final String CREAT_ACCOUNT_BUTTON_TEXT = "Create Account";
    public static final String LOGOUT_BUTTON_TEXT = "Logout";
    public static final String SAVE_BUTTON_TEXT = "Save";
    public static final String CANCEL_BUTTON_TEXT = "Cancel";
    public static final String ADD_APPOINTMENT_BUTTON_TEXT = "Add Appointment";
    public static final String VIEW_APPOINTMENTS_BUTTON_TEXT = "View Appointments";
    public static final String PRESCRIBE_MEDICINE_BUTTON = "Prescribe";
    public static final String CREATE_REFERRAL_BUTTON_TEXT = "Create Referral";
    public static final String PREV_WEEK_BUTTON_TEXT = "← Prev Week";
    public static final String NEXT_WEEK_BUTTON_TEXT = "Next Week →";
    public static final String PREV_DAY_BUTTON_TEXT = "← Prev Day";
    public static final String NEXT_DAY_BUTTON_TEXT = "Next Day →";
    public static final String PATIENT_HISTORY_BUTTON_TEXT = "Patient History";
    public static final String SEND_BUTTON_TEXT = "Send";
    public static final String PRESCRIBE_BUTTON_TEXT = "Prescribe";
    public static final String BUTTON_UPDATE_NOTES = "Update Notes";
    public static final String BUTTON_DELETE_APPOINTMENT = "Delete Appointment";
    public static final String BUTTON_CLOSE = "Close";
    public static final String REGISTER_BUTTON_TEXT = "Register";
    public static final String PROFILE_BUTTON_TEXT = "Profile";
    public static final String BILLING_BUTTON_TEXT = "Billing & Invoicing";
    public static final String CHANGE_PHOTO_BUTTON_TEXT = "Change Photo";
    public static final String EDIT_BUTTON_TEXT = "Edit";
    public static final String NO_PHOTO_PLACEHOLDER_TEXT = "No Photo";

    // ───── Login Screen Text ─────
    public static final String WELCOME_MESSAGE = "WELCOME TO";
    public static final String APP_NAME = "PhysicianConnect";
    public static final String PHYSICIAN_LOGIN_HEADER = "Physician login (email/password):";
    public static final String PHYSICIAN_LOGIN_INFO = "testP@email.com / test123";
    public static final String RECEPTIONIST_LOGIN_HEADER = "Receptionist login (email/password):";
    public static final String RECEPTIONIST_LOGIN_INFO = "testR@email.com / test123";
    public static final String ACCOUNT_TYPE_LABEL = "Account Type:";
    public static final String NAME_LABEL = "Name:";
    public static final String CONFIRM_PASSWORD_LABEL = "Confirm Password:";

    // ─────────── Dialog Titles ───────────
    public static final String CREATE_ACCOUNT_DIALOG_TITLE = "Create Account";
    public static final String LOGIN_DIALOG_TITLE = "PhysicianConnect Login";
    public static final String ADD_APPOINTMENT_DIALOG_TITLE = "Create New Appointment";
    public static final String VIEW_APPOINTMENT_DIALOG_TITLE = "Appointment Details";
    public static final String PATIENT_HISTORY_DIALOG_TITLE = "Patient Medical History";
    public static final String REFERRAL_DIALOG_TITLE = "Manage Referrals";
    public static final String MESSAGES_DIALOG_TITLE = "Messages";
    public static final String CONFIRM_DIALOG_TITLE = "Confirm Deletion";
    public static final String PROFILE_DIALOG_TITLE = "Profile Settings";
    public static final String BILLING_DIALOG_TITLE = "Billing & Invoicing";

    // ─────────── Field Labels & Sections ───────────
    public static final String PATIENT_NAME_LABEL = "Patient Name: ";
    public static final String PATIENT_LABEL = "Patient: ";
    public static final String DATE_LABEL = "Date: ";
    public static final String TIME_LABEL = "Time: ";
    public static final String NOTES_LABEL = "Notes: ";
    public static final String MEDICATION_LABEL = "Medication: ";
    public static final String DOSAGE_LABEL = "Dosage: ";
    public static final String START_DATE_LABEL = "Start Date: ";
    public static final String DURATION_LABEL = "Duration (days): ";
    public static final String REFERRAL_REASON_LABEL = "Reason for Referral:";
    public static final String REFERRAL_TO_LABEL = "Refer To (Physician ID): ";
    public static final String USER_EMAIL_LABEL = "Email: ";
    public static final String USER_PASSWORD_LABEL = "Password: ";
    public static final String SEARCH_RECIPIENT_LABEL = "Search Recipient: ";
    public static final String HISTORY_SECTION_APPOINTMENTS = "Appointments: ";
    public static final String HISTORY_SECTION_PRESCRIPTIONS = "Prescriptions: ";
    public static final String HISTORY_SECTION_REFERRALS = "Referrals: ";
    public static final String HISTORY_LABEL_NOTES = "Notes: ";
    public static final String FREQUENCY_LABEL = "Frequency: ";
    public static final String MEDICINE_LABEL = "Medicine: ";
    public static final String DETAILS_LABEL = "Details: ";
    public static final String TYPE_LABEL = "Type: ";
    public static final String APPOINTMENT_NOTES_LABEL = "Appointment Notes: ";
    public static final String SPECIALTY_LABEL = "Specialty:";
    public static final String OFFICE_HOURS_LABEL = "Office Hours:";
    public static final String PHONE_LABEL = "Phone Number:";
    public static final String ADDRESS_LABEL = "Office Address:";
    public static final String NOTIFICATION_PREFS_LABEL = "Notification Preferences:";
    public static final String NOTIFY_APPOINTMENTS = "Appointments";
    public static final String NOTIFY_BILLING = "Billing";

    // ─────────── Error Messages ───────────
    public static final String ERROR_DIALOG_TITLE = "Error";
    public static final String ERROR_REQUIRED_FIELD = "All fields are required.";
    public static final String ERROR_INVALID_DATE = "Please enter a valid date.";
    public static final String ERROR_INVALID_TIME = "Please enter a valid time.";
    public static final String ERROR_INVALID_EMAIL = "Enter a valid email address.";
    public static final String ERROR_INVALID_DOSAGE = "Dosage must be non-empty.";
    public static final String ERROR_INVALID_DURATION = "Duration must be a positive integer.";
    public static final String ERROR_INVALID_REFERRAL_ID = "Enter a valid physician ID for referral.";
    public static final String ERROR_LOGIN_FAILED = "Login failed. Check credentials.";
    public static final String ERROR_PASSWORD_LENGTH = "Password must be at least 6 characters long.";
    public static final String ERROR_PASSWORD_MISMATCH = "Passwords do not match.";
    public static final String ERROR_EMAIL_EXISTS = "An account with this email already exists.";
    public static final String ERROR_NO_APPOINTMENT_SELECTED = "Please select an appointment to view.";
    public static final String ERROR_DATABASE_OPEN = "Could not open the database";
    public static final String ERROR_INVALID_PHYSICIAN_ID = "Invalid physician ID: must be a number.";
    public static final String ERROR_LAUNCH_FAILED = "Error launching application";
    public static final String ERROR_TIME_CONFLICT = "That time slot is already booked.\nPlease pick a different time.";
    public static final String ERROR_INVALID_INPUT = "Invalid input: ";
    public static final String ERROR_INVALID_NAME = "Please enter a patient name.";
    public static final String ERROR_APPOINTMENT_NOT_FOUND = "Error: could not find appointment to edit.";
    public static final String ERROR_NO_RECIPIENT = "Please select a recipient first";
    public static final String ERROR_REQUIRED_FIELD_REFERRAL = "Patient and type are required.";
    public static final String ERROR_DELETING_APPOINTMENT = "Error deleting appointment: ";
    public static final String ERROR_UPDATING_NOTES = "Error updating notes: ";
    public static final String VALIDATION_ERROR_TITLE = "Validation Error";
    public static final String PHOTO_UPLOAD_FAILED_MSG = "Failed to upload photo: ";

    // ─────────── Photo Error Messages ───────────
    public static final String ERROR_INVALID_IMAGE_FILE = "Invalid image file";
    public static final String ERROR_PHOTO_UPLOAD = "Failed to upload photo: ";
    public static final String ERROR_PHOTO_LOADING = "Error loading photo: ";

    // ─────────── Profile Validation Messages ───────────
    public static final String ERROR_NAME_EMPTY = "Name cannot be empty";
    public static final String ERROR_SPECIALTY_EMPTY = "Specialty cannot be empty";
    public static final String ERROR_OFFICE_HOURS_EMPTY = "Office hours cannot be empty";
    public static final String ERROR_PHONE_EMPTY = "Phone number cannot be empty";
    public static final String ERROR_ADDRESS_EMPTY = "Office address cannot be empty";

    // ─────────── Success Messages ───────────
    public static final String SUCCESS_DIALOG_TITLE = "Success";
    public static final String SUCCESS_ACCOUNT_CREATED = "Account created successfully!";
    public static final String SUCCESS_APPOINTMENT_ADDED = "Appointment added successfully!";
    public static final String SUCCESS_PRESCRIPTION_ADDED = "Prescription added for {patient}: {med}";
    public static final String SUCCESS_REFERRAL_CREATED = "Referral created for {patient}.";
    public static final String PROFILE_UPDATED_MESSAGE = "Profile updated successfully.";

    // ─────────── Messages ───────────
    // When a free slot is clicked: dynamic replacement for date/time
    public static final String FREE_SLOT_MESSAGE = "Slot at {date} {time} is free.\nAdd an appointment?";
    public static final String MESSAGE_NOTES_UPDATED = "Notes updated successfully.";
    public static final String CONFIRM_DELETE_MESSAGE = "Are you sure you want to delete this appointment?";

    // ─────────── DateTime Format ───────────
    public static final String HISTORY_DATE_PATTERN = "MMM dd, yyyy 'at' hh:mm a";
    public static final DateTimeFormatter HISTORY_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("MMM dd, yyyy 'at' hh:mm a");

    // ─────────── Miscellaneous ───────────
    public static final String APP_TITLE = "PhysicianConnect";
    public static final String LOADING_MESSAGE = "Loading...";
    public static final String NO_RECORDS_FOUND = "No records to display.";
    public static final String WELCOME_PREFIX = "Welcome, ";
    public static final String APPOINTMENTS_TITLE = "Your Appointments";
    public static final String PRESCRIBE_MEDICINE_TITLE = "Prescribe Medicine";
    public static final String LABEL_SHOW_DATE = "Show Date: ";
    public static final String LABEL_WEEK_OF = "Week of: ";
    public static final String TAB_DAILY_VIEW = "Daily View";
    public static final String TAB_WEEKLY_VIEW = "Weekly View";
    public static final String MESSAGE_BUTTON_EMOJI = "💬";
    public static final String MESSAGES_TITLE = "Messages";
    public static final String SEARCH_PLACEHOLDER = "Type name or email to search...";
    public static final String ALL_PHYSICIANS_BORDER = "All Physicians";
    public static final String MESSAGES_BORDER = "Messages";
    public static final String TIME_FORMAT_PATTERN = "MMM d, h:mm a";
    public static final String NO_RECIPIENT_SELECTED = "No recipient selected";
    public static final String SELECTED_PREFIX = "Selected: ";
    public static final String UNREAD_SUFFIX = "unread";
    public static final String YOU_LABEL = "You";
    public static final String STATUS_SENT = "✓";
    public static final String STATUS_READ = "✓✓";
    public static final String REFERRALS_HEADER = "Referrals for";
    public static final String REFERRALS_LIST_TITLE = "All Referrals";
    public static final String ADD_APPOINTMENT_CONFIRM_TITLE = "Add Appointment";
    public static final String EMAIL_FIELD_NAME = "emailField";
    public static final String PASSWORD_FIELD_NAME = "passwordField";
    public static final String LOGIN_BUTTON_NAME = "loginBtn";
    public static final String CREATE_ACCOUNT_BUTTON_NAME = "createBtn";

    // ─────────── Receptionist───────────
    public static final String RECEPTIONIST_DASHBOARD_TITLE = "Receptionist Dashboard";
    public static final String ALL_PHYSICIANS_LABEL = "ALL PHYSICIANS";
    public static final String PHYSICIAN_LABEL = "Physician:";
    public static final String SEARCH_PATIENT_LABEL = "Search Patient: ";
    public static final String SEARCH_PATIENT_PLACEHOLDER = "Type patient name...";
    public static final String UNKNOWN_PHYSICIAN_LABEL = "Unknown";
    public static final String ERROR_NO_PHYSICIAN_SELECTED = "Please select a physician to add an appointment.";
    public static final String DATE_FORMAT = "MMM dd, yyyy";
    public static final String TIME_FORMAT = "hh:mm a";
    
    public static final String REVENUE_SUMMARY_HEADER = "Revenue Summary";
    public static final String REVENUE_SUMMARY_COLLAPSED = "►";
    public static final String REVENUE_SUMMARY_EXPANDED = "▼";

    // ─────────── Billing ───────────
    public static final String NEW_INVOICE_BUTTON_TEXT = "New Invoice";
    public static final String REVENUE_SUMMARY_BUTTON_TEXT = "Revenue Summary";
    public static final String TOTAL_LABEL = "Total";
    public static final String BALANCE_LABEL = "Balance";
    public static final String STATUS_LABEL = "Status";
    public static final String APPOINTMENT_LABEL = "Appointment: ";
    public static final String SERVICES_LABEL = "Services:";
    public static final String INSURANCE_TYPE_LABEL = "Insurance Type:";
    public static final String INSURANCE_ADJUSTMENT_LABEL = "Insurance Adjustment:";
    public static final String SELECT_SERVICES_BUTTON_TEXT = "Select Services";
    public static final String NO_SERVICES_SELECTED_LABEL = "No services selected";
    public static final String SERVICES_SELECTED_LABEL = "services selected";
    public static final String SELECT_SERVICES_DIALOG_TITLE = "Select Services";
    public static final String ERROR_NO_SERVICES_SELECTED = "Please select at least one service.";
    public static final String ERROR_NO_INSURANCE_SELECTED = "Please select an insurance type.";
    public static final String ERROR_DUPLICATE_INVOICE = "An invoice already exists for this appointment.";
    public static final String ERROR_INVALID_AMOUNT = "Invalid amount for ";
    public static final String RECORD_PAYMENT_BUTTON_TEXT = "Record Payment";
    public static final String DELETE_INVOICE_BUTTON_TEXT = "Delete Invoice";
    public static final String CONFIRM_DELETE_INVOICE = "Delete this invoice?";
    public static final String INVOICE_DETAILS_DIALOG_TITLE = "Invoice Details";
    public static final String AMOUNT_LABEL = "Amount:";
    public static final String PAYMENT_METHOD_LABEL = "Payment Method:";
    public static final String RECORD_PAYMENT_DIALOG_TITLE = "Record Payment";
    public static final String REVENUE_SUMMARY_FORMAT = "Total Billed: $%.2f\nTotal Paid: $%.2f\nOutstanding: $%.2f";
    public static final String REVENUE_SUMMARY_TITLE = "Revenue Summary";
    public static final String REVENUE_SUMMARY_DIALOG_TITLE = "Revenue Summary";
    public static final String TOTAL_BILLED_LABEL = "Billed";
    public static final String TOTAL_PAID_LABEL = "Paid";
    public static final String OUTSTANDING_LABEL = "Outstanding";
    public static final String CREATED_LABEL = "Created: ";
    public static final String NEW_INVOICE_DIALOG_TITLE = "Create New Invoice";

    // ─────────── Profile Photo ───────────
    public static final String PHOTO_DIR = "src/main/resources/profile_photos";
    public static final String[] SUPPORTED_IMAGE_TYPES = { ".png", ".jpg", ".jpeg" };
    public static final String SELECT_PROFILE_PHOTO_DIALOG_TITLE = "Select Profile Photo";
    public static final String PHYSICIAN_PHOTO_FILTER_DESC = "Physician Profile Photos (*.png, *.jpg, *.jpeg)";
    public static final String RECEPTIONIST_PHOTO_FILTER_DESC = "Receptionist Profile Photos (*.png, *.jpg, *.jpeg)";
    public static final String PHOTO_PREFIX_PHYSICIAN = "p_";
    public static final String PHOTO_PREFIX_RECEPTIONIST = "r_";
    public static final String PHOTO_EXTENSION = ".png";

    // Prevent instantiation
    private UIConfig() {
    }
}