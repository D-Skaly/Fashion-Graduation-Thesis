@echo off
set BASE=d:\CodeFile\Fashion-Graduation-Thesis\backend\src\main\java\com\skaly\fashion_backend\payment

echo Moving domain files...
move "%BASE%\PaymentMethod.java" "%BASE%\domain\"
move "%BASE%\PaymentStatus.java" "%BASE%\domain\"
move "%BASE%\Payment.java" "%BASE%\domain\"
move "%BASE%\PaymentAccessDeniedException.java" "%BASE%\domain\"
move "%BASE%\PaymentNotFoundException.java" "%BASE%\domain\"
move "%BASE%\PaymentCallbackLedger.java" "%BASE%\domain\"

echo Moving application files...
move "%BASE%\PaymentRepository.java" "%BASE%\application\"
move "%BASE%\PaymentCallbackLedgerRepository.java" "%BASE%\application\"
move "%BASE%\PaymentService.java" "%BASE%\application\"

echo Moving interface files...
move "%BASE%\PaymentController.java" "%BASE%\interfaces\"

echo Done!