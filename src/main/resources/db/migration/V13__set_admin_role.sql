-- تعيين role = ADMIN للحساب الإداري
UPDATE users 
SET role = 'ADMIN' 
WHERE email = 'admin@gmail.com';
