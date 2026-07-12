import { useState, useEffect } from 'react';
import { useAssets } from '../hooks/useAssets';
import {
  useBookResource,
  useBookingsForAsset,
  useCancelBooking
} from '../hooks/useOperations';

const TIME_SLOTS = [
  { label: '09:00 - 10:00', startHour: 9, endHour: 10 },
  { label: '10:00 - 11:00', startHour: 10, endHour: 11 },
  { label: '11:00 - 12:00', startHour: 11, endHour: 12 },
  { label: '12:00 - 13:00', startHour: 12, endHour: 13 }
];

export default function ResourceBookingPage() {
  const { data: assetsPage } = useAssets(0, 100);
  const bookableResources = (assetsPage?.content ?? []).filter((a) => a.isBookable);

  // State
  const [selectedAssetId, setSelectedAssetId] = useState('');
  const [bookingDate, setBookingDate] = useState(new Date().toISOString().split('T')[0]);
  const [startHour, setStartHour] = useState('9');
  const [endHour, setEndHour] = useState('10');
  const [purpose, setPurpose] = useState('');

  // Conflict visual helper
  const [hasConflict, setHasConflict] = useState(false);

  // Queries
  const { data: bookings = [], refetch: refetchBookings } = useBookingsForAsset(selectedAssetId);
  const bookMutation = useBookResource();
  const cancelMutation = useCancelBooking();

  // Trigger overlap check locally for UI warning
  useEffect(() => {
    if (selectedAssetId && bookingDate) {
      refetchBookings();
    }
  }, [selectedAssetId, bookingDate]);

  useEffect(() => {
    if (selectedAssetId && startHour && endHour) {
      const sh = Number(startHour);
      const eh = Number(endHour);

      // Check if this ranges overlaps with any existing bookings on bookingDate
      const activeBookings = bookings.filter((b) => b.status !== 'CANCELLED' && b.startTime.startsWith(bookingDate));
      const overlap = activeBookings.some((b) => {
        const bStart = new Date(b.startTime).getHours();
        const bEnd = new Date(b.endTime).getHours();
        return sh < bEnd && eh > bStart;
      });

      setHasConflict(overlap);
    } else {
      setHasConflict(false);
    }
  }, [startHour, endHour, bookings, bookingDate, selectedAssetId]);

  const handleBook = (e) => {
    e.preventDefault();
    if (!selectedAssetId) return;

    const startStr = `${bookingDate}T${startHour.padStart(2, '0')}:00:00`;
    const endStr = `${bookingDate}T${endHour.padStart(2, '0')}:00:00`;

    const payload = {
      assetId: Number(selectedAssetId),
      startTime: startStr,
      endTime: endStr,
      purpose,
    };

    bookMutation.mutate(payload, {
      onSuccess: () => {
        alert('Booking successfully confirmed.');
        refetchBookings();
        setPurpose('');
      },
      onError: (err) => {
        alert(err.response?.data?.message || 'Conflict: Slot unavailable.');
      }
    });
  };

  const handleCancel = (id) => {
    cancelMutation.mutate(id, {
      onSuccess: () => {
        alert('Booking cancelled.');
        refetchBookings();
      }
    });
  };

  const activeBookingsOnDate = bookings.filter(
    (b) => b.status !== 'CANCELLED' && b.startTime.startsWith(bookingDate)
  );

  return (
    <div className="page">
      <div className="page-header">
        <h2>Resource Booking</h2>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1.8fr', gap: '32px' }}>
        {/* Left Form */}
        <div>
          <h3>Reserve Slot</h3>
          <form onSubmit={handleBook}>
            <label>
              Select Resource *
              <select value={selectedAssetId} onChange={(e) => setSelectedAssetId(e.target.value)} required>
                <option value="">Select Resource</option>
                {bookableResources.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.assetTag} — {r.assetName}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Booking Date *
              <input type="date" value={bookingDate} onChange={(e) => setBookingDate(e.target.value)} required />
            </label>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <label>
                Start Hour (24h) *
                <select value={startHour} onChange={(e) => setStartHour(e.target.value)}>
                  <option value="9">09:00</option>
                  <option value="10">10:00</option>
                  <option value="11">11:00</option>
                  <option value="12">12:00</option>
                </select>
              </label>
              <label>
                End Hour (24h) *
                <select value={endHour} onChange={(e) => setEndHour(e.target.value)}>
                  <option value="10">10:00</option>
                  <option value="11">11:00</option>
                  <option value="12">12:00</option>
                  <option value="13">13:00</option>
                </select>
              </label>
            </div>

            <label>
              Purpose
              <input type="text" placeholder="E.g. Team Workshop" value={purpose} onChange={(e) => setPurpose(e.target.value)} />
            </label>

            {hasConflict && (
              <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid #ef4444', borderRadius: '8px', padding: '12px 16px', color: '#fca5a5', fontSize: '13px' }}>
                🚨 <strong>Conflict Detected:</strong> Slot is unavailable due to overlapping booking.
              </div>
            )}

            <button type="submit" className="btn btn-primary" style={{ marginTop: '12px' }} disabled={hasConflict || !selectedAssetId}>
              Confirm Booking
            </button>
          </form>
        </div>

        {/* Right timeline/calendar view */}
        <div>
          <h3>Schedule Timeline ({bookingDate})</h3>
          {selectedAssetId ? (
            <div>
              <div className="booking-grid">
                <div className="booking-hours">
                  <div>09:00</div>
                  <div>10:00</div>
                  <div>11:00</div>
                  <div>12:00</div>
                  <div>13:00</div>
                </div>

                <div className="booking-slots">
                  {activeBookingsOnDate.map((b) => {
                    const startH = new Date(b.startTime).getHours();
                    const endH = new Date(b.endTime).getHours();

                    // Calculate positioning percentiles (9:00 - 13:00 is 4 hours)
                    const topPct = ((startH - 9) / 4) * 100;
                    const heightPct = ((endH - startH) / 4) * 100;

                    return (
                      <div
                        key={b.id}
                        className="booking-bar booking-bar-success"
                        style={{
                          top: `${topPct}%`,
                          height: `${heightPct}%`,
                        }}
                      >
                        <div>
                          <strong>{b.bookedByName}</strong> — {b.purpose || 'Meeting'}
                        </div>
                        <button
                          type="button"
                          style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer', fontWeight: 'bold' }}
                          onClick={() => handleCancel(b.id)}
                        >
                          Cancel
                        </button>
                      </div>
                    );
                  })}

                  {hasConflict && (
                    <div
                      className="booking-bar booking-bar-danger"
                      style={{
                        top: `${((Number(startHour) - 9) / 4) * 100}%`,
                        height: `${((Number(endHour) - Number(startHour)) / 4) * 100}%`,
                        opacity: 0.85,
                        zIndex: 2,
                      }}
                    >
                      <strong>Conflict — Slot Unavailable</strong>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ) : (
            <p style={{ color: '#94a3b8' }}>Select a resource on the left to inspect its timeline slots.</p>
          )}
        </div>
      </div>
    </div>
  );
}
