-- Create volunteers table
CREATE TABLE IF NOT EXISTS volunteers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    aadhar TEXT NOT NULL UNIQUE,
    notes TEXT
);

-- Create meals table
CREATE TABLE IF NOT EXISTS meals (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    serving_size INTEGER NOT NULL
);

-- Create servings table
CREATE TABLE IF NOT EXISTS servings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    volunteer_id INTEGER NOT NULL,
    meal_id INTEGER NOT NULL,
    date TEXT NOT NULL,
    count INTEGER NOT NULL,
    FOREIGN KEY (volunteer_id) REFERENCES volunteers (id),
    FOREIGN KEY (meal_id) REFERENCES meals (id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_volunteer_aadhar ON volunteers (aadhar);
CREATE INDEX IF NOT EXISTS idx_servings_date ON servings (date);
CREATE INDEX IF NOT EXISTS idx_servings_volunteer ON servings (volunteer_id);
CREATE INDEX IF NOT EXISTS idx_servings_meal ON servings (meal_id);