// ── Chip toggle (fix for double-fire bug) ──
document.querySelectorAll('.event-chip input[type="checkbox"]').forEach(cb => {
  cb.addEventListener('change', () => {
    const chip = cb.closest('.event-chip');
    if (cb.checked) chip.classList.add('selected');
    else            chip.classList.remove('selected');
  });
});

// ── Main recommendation function ──
async function getRecommendation() {
  const btn    = document.getElementById('btnRun');
  const loader = document.getElementById('loader');
  const errBox = document.getElementById('errorBox');

  // Collect basic inputs
  const occupancy = parseInt(document.getElementById('occupancy').value);
  const rooms     = parseInt(document.getElementById('rooms').value);
  const covers    = parseInt(document.getElementById('covers').value);
  const shift     = document.getElementById('shift').value;
  const hotelTier = document.getElementById('hotelTier').value;

  // Collect chips — separate on-property events from special circumstances
  const events      = [];
  let vipGuest      = false;
  let cityEvent     = false;
  let longWeekend   = false;

  document.querySelectorAll('.event-chip.selected input').forEach(cb => {
    if      (cb.value === 'vip')         vipGuest    = true;
    else if (cb.value === 'cityEvent')   cityEvent   = true;
    else if (cb.value === 'longWeekend') longWeekend = true;
    else                                 events.push(cb.value);
  });

  // Validation
  if (isNaN(occupancy) || occupancy < 0 || occupancy > 100) {
    showError('Occupancy must be between 0 and 100.'); return;
  }
  if (isNaN(rooms) || rooms < 1) {
    showError('Total rooms must be at least 1.'); return;
  }
  if (isNaN(covers) || covers < 0) {
    showError('Restaurant covers cannot be negative.'); return;
  }

  // UI — loading state
  btn.disabled = true;
  loader.style.display = 'block';
  errBox.style.display = 'none';
  document.getElementById('results').style.display = 'none';

  const payload = {
    occupancyPercent:  occupancy,
    totalRooms:        rooms,
    restaurantCovers:  covers,
    shift,
    hotelTier,
    events,
    vipGuest,
    cityEvent,
    longWeekend
  };

  try {
    const res = await fetch('/api/staffing/recommend', {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(payload)
    });

    if (!res.ok) throw new Error('Server returned ' + res.status);

    const data = await res.json();
    renderResults(data, { hotelTier, vipGuest, cityEvent, longWeekend, events });

  } catch (err) {
    showError('Could not reach the Spring Boot server on port 8080. Is it running? (' + err.message + ')');
  } finally {
    btn.disabled = false;
    loader.style.display = 'none';
  }
}

// ── Render results ──
function renderResults(data, context) {
  // KPI cards
  const total = Object.values(data.departmentStaff).reduce((a, b) => a + b, 0);
  document.getElementById('valTotal').textContent   = total;
  document.getElementById('valSavings').textContent = data.estimatedSavingsPercent.toFixed(1) + '%';
  document.getElementById('valRisk').textContent    = data.overtimeRisk;

  // Risk colour
  const kpiRisk = document.getElementById('kpiRisk');
  kpiRisk.className = 'kpi';
  if (data.overtimeRisk === 'High')   kpiRisk.classList.add('risk-high');
  if (data.overtimeRisk === 'Medium') kpiRisk.classList.add('risk-med');
  if (data.overtimeRisk === 'Low')    kpiRisk.classList.add('risk-low');

  // Department cards
  const icons = {
    'Housekeeping':     '🛏️',
    'Front Office':     '🛎️',
    'F&B / Restaurant': '🍽️',
    'Banquet':          '🥂',
    'Kitchen':          '👨‍🍳',
    'Security':         '🔒',
    'Maintenance':      '🔧'
  };

  const grid = document.getElementById('deptGrid');
  grid.innerHTML = '';
  for (const [dept, count] of Object.entries(data.departmentStaff)) {
    const icon = icons[dept] || '👤';
    grid.innerHTML += `
      <div class="dept-card">
        <div class="dept-name">${icon} ${dept}</div>
        <div class="dept-count">${count}</div>
        <div class="dept-unit">staff</div>
      </div>`;
  }

  // Active flags — show which special signals were factored in
  const flagsDiv = document.getElementById('activeFlags');
  flagsDiv.innerHTML = '';
  const flagLabels = [];
  if (context.vipGuest)    flagLabels.push('👑 VIP Guest factored in');
  if (context.cityEvent)   flagLabels.push('🏟️ City Event factored in');
  if (context.longWeekend) flagLabels.push('📅 Long Weekend factored in');
  if (context.events.includes('wedding'))    flagLabels.push('💍 Wedding factored in');
  if (context.events.includes('conference')) flagLabels.push('🎤 Conference factored in');
  if (context.events.includes('festival'))   flagLabels.push('🪔 Festival factored in');

  if (flagLabels.length > 0) {
    flagsDiv.style.marginTop = '16px';
    flagLabels.forEach(label => {
      flagsDiv.innerHTML += `<span class="flag-badge">${label}</span>`;
    });
  }

  // Recommendation text
  document.getElementById('recBox').textContent = data.recommendation;

  // Show and scroll
  document.getElementById('results').style.display = 'block';
  document.getElementById('results').scrollIntoView({ behavior: 'smooth' });
}

// ── Error helper ──
function showError(msg) {
  const box = document.getElementById('errorBox');
  box.textContent = '⚠️ ' + msg;
  box.style.display = 'block';
}