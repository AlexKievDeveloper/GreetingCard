export const formValidator = {
  isValid,
};

function isValid(fieldName, fieldValue) {
  if (fieldValue === "" || !fieldValue) return true;
  if (fieldName === "firstName" || fieldName === "lastName") {
    return /^[a-zA-Zа-яА-ЯёЁІіЇїґЄє][a-zA-Zа-яА-Я0-9ёЁІіЇїґЄє]{1,15}$/.test(fieldValue);
  } else if (fieldName === "login") {
    return /^[a-zA-Zа-яА-ЯёЁІіЇїґЄє][a-zA-Zа-яА-Я0-9ёЁІіЇїґЄє\-_]{1,15}$/.test(fieldValue);
  } else if (fieldName === "email") {
    return /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/.test(fieldValue);
  } else if (fieldName.indexOf("password") !== -1 && fieldName !== "old_password") {
    return /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,16}$/.test(fieldValue);
  } else {
    return true;
  }
}
