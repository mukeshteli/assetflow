import { useState } from "react";
import { Link } from "react-router-dom";

import AssetStatusBadge from "./AssetStatusBadge";
import DeleteAssetDialog from "./DeleteAssetDialog";
import { useDeleteAsset } from "../../hooks/useAssets";

export default function AssetRow({ asset }) {

    const [open, setOpen] = useState(false);

    const deleteMutation = useDeleteAsset();

    const handleDelete = () => {

        deleteMutation.mutate(asset.id, {

            onSuccess: () => {

                alert("Asset deleted successfully.");

                setOpen(false);

            },

            onError: (error) => {

                alert(
                    error?.response?.data?.message ||
                    "Unable to delete asset."
                );

            }

        });

    };

    return (

        <>

            <tr>

                <td>{asset.assetTag}</td>

                <td>{asset.assetName}</td>

                <td>{asset.categoryName}</td>

                <td>{asset.condition}</td>

                <td>

                    <AssetStatusBadge
                        status={asset.status}
                    />

                </td>

                <td>

                    <Link to={`/assets/edit/${asset.id}`}>
                        View / Edit
                    </Link>

                    {" | "}

                    <button
                        type="button"
                        onClick={() => setOpen(true)}
                        style={{
                            border: "none",
                            background: "transparent",
                            color: "red",
                            cursor: "pointer"
                        }}
                    >
                        Delete
                    </button>

                </td>

            </tr>

            <DeleteAssetDialog

                open={open}

                assetName={asset.assetName}

                onCancel={() => setOpen(false)}

                onDelete={handleDelete}

            />

        </>

    );

}