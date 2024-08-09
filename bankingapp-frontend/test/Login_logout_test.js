
Feature('Login and Logout');

Scenario('Login and Logout Flow', async ({ I }) => {
  I.amOnPage('http://localhost:5173/');

  I.click('Login');

  I.fillField('Username', 'admin');
  I.fillField('Password', 'Admin123*');

  I.click('Login');

  I.amOnPage('http://localhost:5173/admin-dashboard');
 
});
