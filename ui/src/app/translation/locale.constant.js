import MissingTranslateHandler from './translation-handler';

export default angular.module('redTail.locale', [])
    .factory('hmMissingTranslationHandler', MissingTranslateHandler)
    .constant('locales',
        {
            'en_US': {
                "access": {
                    "unauthorized": "Unauthorized",
                    "unauthorized-access": "Unauthorized Access",
                    "unauthorized-access-text": "You should sign in to have access to this resource!",
                    "access-forbidden": "Access Forbidden",
                    "access-forbidden-text": "You haven't access rights to this location!<br/>Try to sign in with different user if you still wish to gain access to this location.",
                    "refresh-token-expired": "Session has expired",
                    "refresh-token-failed": "Unable to refresh session"
                },
                "datetime": {
                    "date-from": "Date from",
                    "time-from": "Time from",
                    "date-to": "Date to",
                    "time-to": "Time to"
                },
                "dialog": {
                    "close": "Close dialog"
                },
                "error": {
                    "unable-to-connect": "Unable to connect to the server! Please check your internet connection.",
                    "unhandled-error-code": "Unhandled error code: {{errorCode}}",
                    "unknown-error": "Unknown error"
                },
                "fullscreen": {
                    "expand": "Expand to fullscreen",
                    "exit": "Exit fullscreen",
                    "toggle": "Toggle fullscreen mode",
                    "fullscreen": "Fullscreen"
                },
                "function": {
                    "function": "Function"
                },
                "home": {
                    "home": "Home",
                    "profile": "Profile",
                    "logout": "Logout",
                    "menu": "Menu",
                    "avatar": "Avatar",
                    "open-user-menu": "Open user menu"
                },
                "layout": {
                    "layout": "Layout",
                    "manage": "Manage layouts",
                    "settings": "Layout settings",
                    "color": "Color",
                    "main": "Main",
                    "right": "Right",
                    "select": "Select target layout"
                },
                "legend": {
                    "position": "Legend position",
                    "show-max": "Show max value",
                    "show-min": "Show min value",
                    "show-avg": "Show average value",
                    "show-total": "Show total value",
                    "settings": "Legend settings",
                    "min": "min",
                    "max": "max",
                    "avg": "avg",
                    "total": "total"
                },
                "login": {
                    "login": "Login",
                    "request-password-reset": "Request Password Reset",
                    "reset-password": "Reset Password",
                    "create-password": "Create Password",
                    "passwords-mismatch-error": "Entered passwords must be same!",
                    "password-again": "Password again",
                    "sign-in": "Please sign in",
                    "username": "Username (email)",
                    "remember-me": "Remember me",
                    "forgot-password": "Forgot Password?",
                    "password-reset": "Password reset",
                    "new-password": "New password",
                    "new-password-again": "New password again",
                    "password-link-sent-message": "Password reset link was successfully sent!",
                    "email": "Email"
                },
                "position": {
                    "top": "Top",
                    "bottom": "Bottom",
                    "left": "Left",
                    "right": "Right"
                },
                "profile": {
                    "profile": "Profile",
                    "change-password": "Change Password",
                    "current-password": "Current password"
                },
                "value": {
                    "type": "Value type",
                    "string": "String",
                    "string-value": "String value",
                    "integer": "Integer",
                    "integer-value": "Integer value",
                    "invalid-integer-value": "Invalid integer value",
                    "double": "Double",
                    "double-value": "Double value",
                    "boolean": "Boolean",
                    "boolean-value": "Boolean value",
                    "false": "False",
                    "true": "True"
                },
                "icon": {
                    "icon": "Icon",
                    "select-icon": "Select icon",
                    "material-icons": "Material icons",
                    "show-all": "Show all icons"
                },
                "language": {
                    "language": "Language",
                    "en_US": "English"
                }
            }
        }
    ).name;
