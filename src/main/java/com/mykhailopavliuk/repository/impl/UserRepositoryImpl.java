package com.mykhailopavliuk.repository.impl;

import com.mykhailopavliuk.model.User;
import com.mykhailopavliuk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final File databaseFile;

    @Autowired
    public UserRepositoryImpl(File databaseFile) {
        this.databaseFile = databaseFile;
    }

    @Override
    public <S extends User> S save(S user) {
        Long userId = user.getId();
        if (!existsById(userId)) {
            writeToDatabase(user);
        } else {
            String line;
            String[] userData;
            StringBuilder updatedFile = new StringBuilder();

            try (var reader = new BufferedReader(new FileReader(databaseFile))) {
                while ((line = reader.readLine()) != null) {
                    userData = line.split(",");

                    if (userData[0].equals(userId.toString())) {
                        updatedFile.append(userId);
                        updatedFile.append(",");
                        updatedFile.append(user.getEmail());
                        updatedFile.append(",");
                        updatedFile.append(String.valueOf(user.getPassword()));
                        updatedFile.append("\n");
                    } else {
                        updatedFile.append(line).append("\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (var writer = new BufferedWriter(new FileWriter(databaseFile))) {
                writer.write(updatedFile.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> users) {
        for (User user : users) {
            writeToDatabase(user);
        }
        return null;
    }

    private void writeToDatabase(User user) {
        try (var writer = new BufferedWriter(new FileWriter(databaseFile, true))) {
            writer.append(String.valueOf(user.getId()));
            writer.append(",");
            writer.append(user.getEmail());
            writer.append(",");
            writer.append(String.valueOf(user.getPassword()));
            writer.append("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String line;
        String[] userData;

        try (var reader = new BufferedReader(new FileReader(databaseFile))) {
            while ((line = reader.readLine()) != null) {
                userData = line.split(",");

                if (userData[0].equals(id.toString())) {
                    return Optional.of(new User(Long.parseLong(userData[0]), userData[1], userData[2].toCharArray(), null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        String line;
        String[] userData;

        try (var reader = new BufferedReader(new FileReader(databaseFile))) {
            while ((line = reader.readLine()) != null) {
                userData = line.split(",");

                if (userData[0].equals(id.toString())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<User> findAll() {
        String line;
        String[] userData;
        List<User> users = new ArrayList<>();

        try (var reader = new BufferedReader(new FileReader(databaseFile))) {
            while ((line = reader.readLine()) != null) {
                userData = line.split(",");
                users.add(new User(Long.parseLong(userData[0]), userData[1], userData[2].toCharArray(), null));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public long count() {
        long count = 0;

        try (var reader = new BufferedReader(new FileReader(databaseFile))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    @Override
    public void deleteById(Long id) {
        String line;
        String[] userData;
        StringBuilder updatedFile = new StringBuilder();

        try (var reader = new BufferedReader(new FileReader(databaseFile))) {
            while ((line = reader.readLine()) != null) {
                userData = line.split(",");

                if (!userData[0].equals(id.toString())) {
                    updatedFile.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (var writer = new BufferedWriter(new FileWriter(databaseFile))) {
            writer.write(updatedFile.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        try (var writer = new BufferedWriter(new FileWriter(databaseFile))) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String line;
        String[] userData;

        try (var reader = new BufferedReader(new FileReader(databaseFile))) {
            while ((line = reader.readLine()) != null) {
                userData = line.split(",");

                if (userData[1].equals(email)) {
                    return Optional.of(new User(Long.parseLong(userData[0]), userData[1], userData[2].toCharArray(), null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public long getAvailableId() {
        String line;
        String[] userData = null;

        try (var reader = new BufferedReader(new FileReader(databaseFile))) {
            while ((line = reader.readLine()) != null) {
                userData = line.split(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userData != null ? (Long.parseLong(userData[0]) + 1) : 1;
    }
}
