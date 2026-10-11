function formatPrice(value) {
  const num = Number(value);
  if (Number.isNaN(num)) {
    return '0.00';
  }
  return num.toFixed(2);
}

function formatTime(value) {
  if (!value) {
    return '';
  }
  return String(value).replace('T', ' ').slice(0, 19);
}

module.exports = { formatPrice, formatTime };

