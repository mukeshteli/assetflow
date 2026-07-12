import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAsset, useCreateAsset, useUpdateAsset } from '../../hooks/useAssets';
import { useAssetCategories } from '../../hooks/useAssetCategories';

const CONDITIONS = ['NEW', 'GOOD', 'FAIR', 'POOR', 'DAMAGED'];

const emptyForm = {
  assetName: '',
  serialNumber: '',
  manufacturer: '',
  model: '',
  purchaseDate: '',
  purchaseCost: '',
  warrantyExpiry: '',
  currentLocation: '',
  condition: 'NEW',
  isBookable: false,
  notes: '',
  categoryId: '',
};

export default function RegisterAssetPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const editing = Boolean(id);

  const { data: asset } = useAsset(id);
  const { data: categories = [], isLoading: categoriesLoading } = useAssetCategories();
  const createAssetMutation = useCreateAsset();
  const updateAssetMutation = useUpdateAsset();

  const [formData, setFormData] = useState(emptyForm);

  useEffect(() => {
    if (asset) {
      setFormData({
        assetName: asset.assetName,
        serialNumber: asset.serialNumber,
        manufacturer: asset.manufacturer ?? '',
        model: asset.model ?? '',
        purchaseDate: asset.purchaseDate ?? '',
        purchaseCost: asset.purchaseCost ?? '',
        warrantyExpiry: asset.warrantyExpiry ?? '',
        currentLocation: asset.currentLocation ?? '',
        condition: asset.condition,
        isBookable: asset.isBookable,
        notes: asset.notes ?? '',
        categoryId: asset.categoryId,
      });
    }
  }, [asset]);

  function handleChange(event) {
    const { name, value, type, checked } = event.target;
    setFormData((previous) => ({
      ...previous,
      [name]: type === 'checkbox' ? checked : value,
    }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    const saveMutation = editing ? updateAssetMutation : createAssetMutation;
    const payload = editing ? { id, asset: formData } : formData;

    saveMutation.mutate(payload, {
      onSuccess: () => navigate('/assets'),
    });
  }

  const isSaving = createAssetMutation.isPending || updateAssetMutation.isPending;

  return (
    <div className="container">
      <h2>{editing ? 'Edit Asset' : 'Register Asset'}</h2>

      {editing && asset && (
        <p className="auth-hint">Asset tag: {asset.assetTag} (system-generated, not editable)</p>
      )}

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="assetName"
          placeholder="Asset Name"
          value={formData.assetName}
          onChange={handleChange}
          required
        />

        <input
          type="text"
          name="serialNumber"
          placeholder="Serial Number"
          value={formData.serialNumber}
          onChange={handleChange}
          required
        />

        <input
          type="text"
          name="manufacturer"
          placeholder="Manufacturer"
          value={formData.manufacturer}
          onChange={handleChange}
        />

        <input
          type="text"
          name="model"
          placeholder="Model"
          value={formData.model}
          onChange={handleChange}
        />

        <input type="date" name="purchaseDate" value={formData.purchaseDate} onChange={handleChange} />

        <input
          type="number"
          name="purchaseCost"
          placeholder="Purchase Cost"
          value={formData.purchaseCost}
          onChange={handleChange}
        />

        <input type="date" name="warrantyExpiry" value={formData.warrantyExpiry} onChange={handleChange} />

        <input
          type="text"
          name="currentLocation"
          placeholder="Current Location"
          value={formData.currentLocation}
          onChange={handleChange}
        />

        <select name="condition" value={formData.condition} onChange={handleChange} required>
          {CONDITIONS.map((c) => (
            <option key={c} value={c}>
              {c}
            </option>
          ))}
        </select>

        <label>
          <input
            type="checkbox"
            name="isBookable"
            checked={formData.isBookable}
            onChange={handleChange}
          />
          Shared / bookable resource
        </label>

        <textarea
          name="notes"
          rows="4"
          placeholder="Notes"
          value={formData.notes}
          onChange={handleChange}
        />

        <select name="categoryId" value={formData.categoryId} onChange={handleChange} required>
          <option value="">{categoriesLoading ? 'Loading categories…' : 'Select category'}</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>

        <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
          <button type="button" onClick={() => navigate('/assets')}>
            Cancel
          </button>

          <button type="submit" disabled={isSaving}>
            {isSaving ? 'Saving…' : editing ? 'Update Asset' : 'Register Asset'}
          </button>
        </div>
      </form>
    </div>
  );
}