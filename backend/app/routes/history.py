from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from datetime import datetime, timezone
from app import db

history_bp = Blueprint("history", __name__)


@history_bp.post("/")
@jwt_required()
def record_transfer():
    user_id = get_jwt_identity()
    data = request.get_json(silent=True) or {}

    required = ("transfer_id", "peer_device_id", "file_name", "size_bytes", "direction")
    if any(data.get(f) is None for f in required):
        return jsonify(error=f"campi obbligatori: {', '.join(required)}"), 400

    direction = data["direction"]
    if direction not in ("SENT", "RECEIVED"):
        return jsonify(error="direction deve essere SENT o RECEIVED"), 400

    group_code = data.get("group_code", "").strip().upper() or None
    if group_code:
        group = db.groups.find_one({"code": group_code})
        if not group:
            return jsonify(error="codice gruppo non valido"), 404
        if user_id not in group["members"]:
            return jsonify(error="non sei membro del gruppo"), 403

    entry = {
        "transfer_id": data["transfer_id"],
        "user_id": user_id,
        "group_code": group_code,
        "peer_device_id": data["peer_device_id"],
        "file_name": data["file_name"],
        "size_bytes": int(data["size_bytes"]),
        "direction": direction,
        "latitude": data.get("latitude"),
        "longitude": data.get("longitude"),
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }
    db.transfers.update_one(
        {"transfer_id": entry["transfer_id"]},
        {"$set": entry},
        upsert=True,
    )
    entry.pop("_id", None)
    return jsonify(entry), 201


@history_bp.get("/")
@jwt_required()
def get_history():
    user_id = get_jwt_identity()
    group_code = request.args.get("group_code", "").strip().upper() or None

    if group_code:
        group = db.groups.find_one({"code": group_code})
        if not group:
            return jsonify(error="codice gruppo non valido"), 404
        if user_id not in group["members"]:
            return jsonify(error="non sei membro del gruppo"), 403
        query = {"group_code": group_code}
    else:
        query = {"user_id": user_id}

    transfers = list(db.transfers.find(query, {"_id": 0}, sort=[("timestamp", -1)]))
    return jsonify(transfers), 200
