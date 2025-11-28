package com.example.pdp_project.service;

import com.example.pdp_project.dto.request.LoginRequest;
import com.example.pdp_project.dto.request.TripDTO;
import com.example.pdp_project.dto.response.LoginResponse;
import com.example.pdp_project.entity.*;
import com.example.pdp_project.repo.*;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BotService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TelegramBot bot = new TelegramBot("8214948566:AAFjTm8enotbifrF8mD-5Bb2kS_weOcN-R8");
    private final CategoryRepository categoryRepository;
    private final TgUserRepository tgUserRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final AttachmentRepository attachmentRepository;
    private Map<Long, Integer> reffMap = new HashMap<>();
    private TripDTO tripDTO = new TripDTO();
    String adminChatId = "6377137532";

    public void MessageService(Message message) {
        Long chatId = message.chat().id();
        String text = message.text();
        String username = message.from().username();
        TgUser tgUser = getOrCreateTgUser(chatId, username);

        if (chatId.equals(Long.parseLong(adminChatId))) {
            if (message != null) {
                ensureDeleteList(tgUser);
                if (message.messageId() != null) {
                    tgUser.getDeletedMessageIds().add(message.messageId());
                    tgUserRepository.save(tgUser);
                }


                if (text != null && text.equals("/start")) {
                    SendMessage sendMessage = new SendMessage(
                            chatId,
                            "Assalamu aleykum siz bu yerda adminsiz , Trip qoshishni istasangiz trip qilish buttonini bosing !"
                    );
                    sendMessage.replyMarkup(new ReplyKeyboardMarkup("Trip qoshish").resizeKeyboard(true));
                    tgUser.setState("ADD_TRIP");

                    SendResponse response = bot.execute(sendMessage);
                    saveMessageId(tgUser, response);

                } else if (tgUser.getState().equals("ADD_TRIP")) {
                    if (text != null && text.equals("Trip qoshish")) {
                        SendMessage sendMessage = new SendMessage(chatId, "Trip uchun title kiriting:");
                        sendMessage.replyMarkup(new ReplyKeyboardRemove());
                        tgUser.setState("ADD_TRIP_TITLE");

                        SendResponse response = bot.execute(sendMessage);
                        saveMessageId(tgUser, response);
                    }

                } else if (tgUser.getState().equals("ADD_TRIP_TITLE")) {
                    if (text != null) {
                        tripDTO.setTitle(text);
                        SendMessage sendMessage = new SendMessage(chatId, "Ajoyib endi esa descriptionni:");
                        tgUser.setState("ADD_TRIP_DESCRIPTION");

                        SendResponse response = bot.execute(sendMessage);
                        saveMessageId(tgUser, response);
                    }

                } else if (tgUser.getState().equals("ADD_TRIP_DESCRIPTION")) {
                    if (text != null) {
                        tripDTO.setDescription(text);
                        SendMessage sendMessage = new SendMessage(chatId, "Ajoyib endi countryni tanlang:");
                        sendMessage.replyMarkup(new ReplyKeyboardMarkup(generateCountryButtons()));
                        tgUser.setState("ADD_TRIP_COUNTRY");

                        SendResponse response = bot.execute(sendMessage);
                        saveMessageId(tgUser, response);
                    }

                } else if (tgUser.getState().equals("ADD_TRIP_COUNTRY")) {
                    if (text != null) {
                        try {
                            tripDTO.setCountry(text);
                            SendMessage sendMessage = new SendMessage(chatId, "Ajoyib endi narxini kiriting:");
                            sendMessage.replyMarkup(new ReplyKeyboardRemove());
                            tgUser.setState("ADD_TRIP_PRICE");

                            SendResponse response = bot.execute(sendMessage);
                            saveMessageId(tgUser, response);
                        } catch (NumberFormatException e) {
                            SendMessage sendMessage = new SendMessage(chatId, "Son kiritishda xatolik!");
                            SendResponse response = bot.execute(sendMessage);
                            saveMessageId(tgUser, response);
                        }
                    }

                } else if (tgUser.getState().equals("ADD_TRIP_PRICE")) {
                    if (text != null) {
                        tripDTO.setPrice(Double.parseDouble(text));
                        SendMessage sendMessage = new SendMessage(chatId, "Endi esa trip uchun category tanlang:");
                        sendMessage.replyMarkup(new ReplyKeyboardMarkup(getCategoryMatrix2(categoryRepository.findAll())).resizeKeyboard(true));
                        tgUser.setState("ADD_TRIP_CATEGORY");

                        SendResponse response = bot.execute(sendMessage);
                        saveMessageId(tgUser, response);
                    }

                } else if (tgUser.getState().equals("ADD_TRIP_CATEGORY")) {
                    if (text != null) {
                        Category category = categoryRepository.findByName(text);
                        if (category != null) {
                            tripDTO.setCategoryId(category.getId());
                            SendMessage sendMessage = new SendMessage(chatId, "Endi esa trip uchun rasm kiriting:");
                            sendMessage.replyMarkup(new ReplyKeyboardRemove());
                            tgUser.setState("ADD_TRIP_RASM");

                            SendResponse response = bot.execute(sendMessage);
                            saveMessageId(tgUser, response);
                        }
                    }

                } else if (tgUser.getState().equals("ADD_TRIP_RASM")) {
                    if (message.photo() != null) {
                        PhotoSize[] photos = message.photo();
                        PhotoSize photo = photos[photos.length - 1];
                        String fileId = photo.fileId();

                        try {
                            GetFile getFile = new GetFile(fileId);
                            GetFileResponse fileResponse = bot.execute(getFile);
                            if (fileResponse.isOk()) {
                                com.pengrad.telegrambot.model.File file = fileResponse.file();
                                String filePath = file.filePath();
                                String fileUrl = "https://api.telegram.org/file/bot" + bot.getToken() + "/" + filePath;
                                byte[] imageBytes = downloadFileAsByteArray(fileUrl);

                                Attachment attachment = new Attachment();
                                attachment.setContent(imageBytes);
                                attachmentRepository.save(attachment);

                                Trip trip = new Trip(tripDTO);
                                trip.setPhoto(attachment);
                                trip.setCategory(categoryRepository.findById(tripDTO.getCategoryId()).get());
                                trip.setRating(1.0);
                                tripRepository.save(trip);

                                addTripForChannel(trip);
                                advertisingEveryone(trip);

                                // delete all previous messages
                                for (Integer deleteMessageId : tgUser.getDeletedMessageIds()) {
                                    try {
                                        DeleteMessage deleteMessage = new DeleteMessage(chatId, deleteMessageId);
                                        bot.execute(deleteMessage);
                                    } catch (Exception e) {
                                        System.out.println("Delete error: " + e.getMessage());
                                    }
                                }
                                tgUser.getDeletedMessageIds().clear();

                                // send final message
                                SendMessage sendMessage = new SendMessage(chatId, "Trip muvaffaqqiyatli qoshildi !");
                                sendMessage.replyMarkup(new ReplyKeyboardMarkup("Trip qoshish").resizeKeyboard(true));
                                tgUser.setState("ADD_TRIP");

                                SendResponse response = bot.execute(sendMessage);
                                saveMessageId(tgUser, response);
                            }

                        } catch (Exception e) {
                            System.out.println("Xato yuz berdi: " + e.getMessage());
                        }
                    }
                }
            }


        } else {
            if (text != null && text.startsWith("/start")) {
                System.out.println("======================================================");
                System.out.println("======================================================");
                System.out.println(text);
                System.out.println("======================================================");
                String[] parts = text.split(" ");
                if (text.equals("/start")) {
                    SendMessage sendMessage = new SendMessage(
                            chatId,
                            "Asslomu aleykum botimizga xush kelibsiz"
                    );
                    sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDCCB MENU").resizeKeyboard(true));

                    tgUser.setState("MENU");
                    tgUser.setDeletedMessageIdNext(message.messageId());
                    tgUserRepository.save(tgUser);
                    bot.execute(sendMessage);
                }
                if (parts.length > 1 && parts[1].startsWith("invite_")) {
                    try {
                        int refId = Integer.parseInt(parts[1].substring("invite_".length()));
                        System.out.println(refId);
                        reffMap.put(chatId, refId); //
                    } catch (NumberFormatException e) {
                        System.out.println("Referal ID noto‘g‘ri formatda!");
                    }
                }
                if (text.startsWith("/start send_")) {
                    String data = text.replace("/start send_", ""); // "12345_6789"
                    String[] ids = data.split("_"); // ["12345", "6789"]

                    if (ids.length == 2) {
                        String refUserStr = ids[0];
                        String tripIdStr = ids[1];

                        if (!refUserStr.isBlank() && !tripIdStr.isBlank()) {
                            Long refUserId = Long.parseLong(refUserStr);
                            Long tripId = Long.parseLong(tripIdStr);

                            Trip recommendedTrip = tripRepository.findById(tripId).orElse(null);
                            if (recommendedTrip != null) {
                                SendPhoto recommendMsg = new SendPhoto(chatId,
                                        recommendedTrip.getPhoto().getContent());

                                recommendMsg.caption("📢 Sayohat qiling\n\n" +
                                        "📌 " + recommendedTrip.getTitle() + "\n" +
                                        "📍 " + recommendedTrip.getCountry() + "\n" +
                                        "💵 Narx: " + recommendedTrip.getPrice() + " so'm");

                                recommendMsg.replyMarkup(new InlineKeyboardMarkup(
                                        new InlineKeyboardButton("Ko‘rish").callbackData("korish"),
                                        new InlineKeyboardButton("Kerak emas").callbackData("kerak_emas")
                                ));
                                tgUser.setAdSelectedTripId(tripId);
                                tgUser.setRefIdNext(refUserId.intValue());
                                tgUser.setState("ORDER");
                                tgUser.setDeletedMessageId(bot.execute(recommendMsg).message().messageId());
                                tgUserRepository.save(tgUser);
                                ;
                            }
                        }
                    }
                }
                if (text.startsWith("/start add_")) {
                    String[] s = text.split("_");
                    Long tripId = Long.parseLong((s[1]));
                    Trip recommendedTrip = tripRepository.findById(tripId).orElse(null);
                    if (recommendedTrip != null) {
                        SendPhoto recommendMsg = new SendPhoto(chatId,
                                recommendedTrip.getPhoto().getContent());

                        recommendMsg.caption("📢 Sayohat qiling\n\n" +
                                "📌 " + recommendedTrip.getTitle() + "\n" +
                                "📍 " + recommendedTrip.getCountry() + "\n" +
                                "💵 Narx: " + recommendedTrip.getPrice() + " so'm");

                        recommendMsg.replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton("Ko‘rish").callbackData("korish"),
                                new InlineKeyboardButton("Kerak emas").callbackData("kerak_emas")
                        ));

                        tgUser.setAdSelectedTripId(tripId);
                        tgUser.setState("ORDER");
                        tgUser.setDeletedMessageId(bot.execute(recommendMsg).message().messageId());
                        tgUserRepository.save(tgUser);
                    }
                }
                return;
            }
            if (tgUser.getState().equals("MENU")) {
                if (text != null) {
                    SendMessage sendMessage = new SendMessage(chatId, "quyidagilardan birini tanlang");
                    sendMessage.replyMarkup(genarateMenuButtons());
                    tgUser.setState("TRIP_QILISH");
                    tgUserRepository.save(tgUser);
                    bot.execute(sendMessage);
                }
            } else if (tgUser.getState().equals("TRIP_QILISH")) {
                if (text != null && text.equals("Trip qilish")) {
                    tgUser.setState("CATEGORY_LIST");
                    tgUserRepository.save(tgUser);

                    List<Category> categories = categoryRepository.findAll();


                    ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(
                            getCategoryMatrix(categories)
                    );
                    markup.resizeKeyboard(true);
                    SendMessage sendMessage = new SendMessage(chatId, "🗂 Kategoriyalardan birini tanlang:");
                    sendMessage.replyMarkup(markup);
                    bot.execute(sendMessage);
                } else if (text != null && text.equals("Mening Triplarim")) {
                    List<Booking> bookings = bookingRepository.findAllByUserOrderByBookingDateDesc(tgUser);

                    if (bookings.isEmpty()) {
                        SendMessage sendMessage = new SendMessage(chatId, "Siz hali hech qanday trip buyurtma qilmagansiz.");
                        sendMessage.replyMarkup(genarateMenuButtons());
                        tgUser.setState("TRIP_QILISH");
                        tgUserRepository.save(tgUser);
                        bot.execute(sendMessage);
                        return;
                    }

                    StringBuilder history = new StringBuilder("🧾 *Sizning buyurtmalar tarixi:*\n\n");

                    for (Booking booking : bookings) {
                        Trip trip = booking.getTrip();
                        history.append("🌍 *" + trip.getCountry() + "*\n");
                        history.append("📅 Sana: " + booking.getBookingDate() + "\n");
                        history.append("📆 Kunlar soni: " + booking.getDays() + "\n");
                        history.append("💵 Narx (1 kun): " + trip.getPrice() + " so'm\n");
                        history.append("💰 Umumiy: " + (trip.getPrice() * booking.getDays()) + " so'm\n");
                        history.append("———————————————\n");
                    }

                    SendMessage sendMessage = new SendMessage(chatId, history.toString());
                    sendMessage.parseMode(ParseMode.Markdown);
                    bot.execute(sendMessage);
                } else if (text != null && text.equals("Hisobni toldirish")) {
                    Double balance = tgUser.getBalance();
                    if (balance == null) balance = 0.0;

                    String messageText = "💰 Hisobingizdagi mablag': " + String.format("%,.0f", balance) + " so'm.\n" +
                            "Hisobingizni to‘ldirish uchun miqdorni yuboring.";

                    SendMessage sendMessage = new SendMessage(chatId, messageText);
                    bot.execute(sendMessage);
                    tgUser.setState("TOP_UP");
                    tgUserRepository.save(tgUser);
                } else if (text != null && text.equals("👥 Do‘stni taklif qilish")) {
                    String inviteLink = "https://t.me/" + "pdp_exam_project_bot" + "?start=invite_" + tgUser.getId();

                    String link = """
                            👥 Do‘stingizni taklif qilish uchun quyidagi havolani ulashing:
                            
                            %s
                            
                            Agar do‘stingiz shu havola orqali kirib, ushbu sayohatni xarid qilsa — siz balansingizga 10%% bonus olasiz! 🎁
                            """.formatted(inviteLink);
                    SendMessage sendMessage = new SendMessage(chatId, link);
                    bot.execute(sendMessage);
                }
            } else if (tgUser.getState().equals("CATEGORY_LIST")) {
                if (text != null && text.equals("BACK")) {
                    SendMessage sendMessage = new SendMessage(
                            chatId,
                            "Quyidagilardan birini tanlang:"
                    );
                    sendMessage.replyMarkup(genarateMenuButtons());
                    bot.execute(sendMessage);
                    tgUser.setState("TRIP_QILISH");
                    tgUserRepository.save(tgUser);
                    return;
                }
                Category category = categoryRepository.findByName(text);

                if (category == null) {
                    sendMessage(chatId, "❌ Kategoriya topilmadi.");
                    return;
                }

                List<Trip> tripList = tripRepository.findAllByCategory(category);
                if (tripList.isEmpty()) {
                    SendMessage sendMessage = new SendMessage(
                            chatId,
                            "⚠ Bu kategoriyada hech qanday trip yo‘q."
                    );
                    sendMessage.replyMarkup(new ReplyKeyboardMarkup(getCategoryMatrix(categoryRepository.findAll())).resizeKeyboard(true));
                    bot.execute(sendMessage);
                    return;
                }

                tgUser.setCategoryId(category.getId());
                tgUserRepository.save(tgUser);

                System.out.println(tripList);

                ReplyKeyboardMarkup tripButtons = new ReplyKeyboardMarkup(
                        generateTripMatrix(tripList)
                );

                SendMessage sendMessage = new SendMessage(chatId, "📋 Triplar ro‘yxati:");
                sendMessage.replyMarkup(tripButtons);
                bot.execute(sendMessage);
                System.out.println(sendMessage);
                tgUser.setState("TRIP_LIST");
                tgUserRepository.save(tgUser);
            } else if (tgUser.getState().equals("TRIP_LIST")) {
                if (text != null && text.equals("⬅️ Orqaga")) {
                    SendMessage sendMessage = new SendMessage(
                            chatId,
                            "🗂 Kategoriyalardan birini tanlang:"
                    );
                    sendMessage.replyMarkup(new ReplyKeyboardMarkup(getCategoryMatrix(categoryRepository.findAll())).resizeKeyboard(true));
                    bot.execute(sendMessage);
                    tgUser.setState("CATEGORY_LIST");
                    tgUserRepository.save(tgUser);
                    return;
                }
                Trip trip = tripRepository.findByTitle((text));

                if (trip == null) {
                    sendMessage(chatId, "❌ Trip topilmadi.");
                    return;
                }

                tgUser.setState("TRIP_DETAIL");
                tgUser.setSelectedTripId(trip.getId());
                tgUser.setAdSelectedTripId(trip.getId());
                tgUserRepository.save(tgUser);
                SendMessage removeKeyboard = new SendMessage(chatId, "\uD83D\uDD3D Tafsilotlar yuklanmoqda...");
                removeKeyboard.replyMarkup(new ReplyKeyboardRemove(true));
                bot.execute(removeKeyboard);
                int days = 1;
                String caption = getTripCaption(trip, days);
                InlineKeyboardMarkup inlineKeyboard = getTripInlineKeyboard(trip.getId(), days);

                SendPhoto sendPhoto = new SendPhoto(chatId, trip.getPhoto().getContent());
                sendPhoto.caption(caption);
                sendPhoto.replyMarkup(inlineKeyboard);
                bot.execute(sendPhoto);
                tgUser.setState("TRIP_DETAIL");
                tgUserRepository.save(tgUser);
            } else if (tgUser.getState().equals("ORDER")) {
                if (tgUser.getDeletedMessageId() != null) {
                    DeleteMessage deleteMessage = new DeleteMessage(
                            chatId,
                            tgUser.getDeletedMessageId()
                    );
                    tgUser.setDeletedMessageId(null);
                    tgUserRepository.save(tgUser);
                    bot.execute(deleteMessage);
                }
                if (text != null && text.equals("Trip qilish")) {
                    List<Trip> tripById = tripRepository.getTripById(tgUser.getAdSelectedTripId());
                    SendPhoto sendPhoto = new SendPhoto(chatId, tripById.get(0).getPhoto().getContent());
                    int days = 1;
                    String caption = getTripCaption(tripById.get(0), days);
                    InlineKeyboardMarkup inlineKeyboard = getTripInlineKeyboard(tripById.get(0).getId(), days);
                    sendPhoto.caption(caption);
                    sendPhoto.replyMarkup(inlineKeyboard);
                    tgUser.setState("TRIP_DETAIL");
                    tgUserRepository.save(tgUser);
                    bot.execute(sendPhoto);
                } else if (text != null && text.equals("Kerak emas")) {
                    SendMessage sendMessage = new SendMessage(chatId, "Menu");
                    sendMessage.replyMarkup(genarateMenuButtons());
                    tgUser.setState("TRIP_QILISH");
                    tgUserRepository.save(tgUser);
                    bot.execute(sendMessage);
                }
            } else if (tgUser.getState().equals("TOP_UP")) {
                try {
                    Long balance = Long.valueOf(text);
                    if (tgUser.getBalance() == null) {
                        tgUser.setBalance(0.0);
                    }
                    tgUser.setBalance(tgUser.getBalance() + balance);
                    tgUserRepository.save(tgUser);
                    SendMessage sendMessage = new SendMessage(
                            chatId,
                            "Hisobingiz toldirildi ✅ " +
                                    "\nSizning balansinhgiz 💵 : " + tgUser.getBalance()
                    );
                    sendMessage.replyMarkup(genarateMenuButtons());
                    tgUser.setState("TRIP_QILISH");
                    tgUserRepository.save(tgUser);
                    bot.execute(sendMessage);
                } catch (Exception e) {
                    SendMessage sendMessage = new SendMessage(
                            chatId,
                            "Son kiritinggggg !!!!"
                    );
                    bot.execute(sendMessage);
                }
            }

        }
    }

    private String[][] generateCountryButtons() {
        return new String[][]{
                {"🇺🇸 United States", "🇬🇧 United Kingdom"},
                {"🇩🇪 Germany", "🇫🇷 France"},
                {"🇮🇹 Italy", "🇪🇸 Spain"},
                {"🇷🇺 Russia", "🇨🇳 China"},
                {"🇯🇵 Japan", "🇰🇷 South Korea"},
                {"🇺🇿 Uzbekistan", "🇰🇿 Kazakhstan"},
                {"🇹🇷 Turkey", "🇦🇪 UAE"},
                {"🇸🇦 Saudi Arabia", "🇮🇳 India"},
                {"🇵🇰 Pakistan", "🇧🇩 Bangladesh"},
                {"🇮🇩 Indonesia", "🇲🇾 Malaysia"},
                {"🇹🇭 Thailand", "🇻🇳 Vietnam"},
                {"🇧🇷 Brazil", "🇲🇽 Mexico"},
                {"🇨🇦 Canada", "🇦🇺 Australia"},
                {"🇳🇿 New Zealand", "🇿🇦 South Africa"},
                {"🇪🇬 Egypt", "🇲🇦 Morocco"},
                {"🇳🇬 Nigeria", "🇰🇪 Kenya"}
        };
    }

    private String[] getCategoryMatrix2(List<Category> categories) {
        List<String> result = new ArrayList<>();
        for (Category category : categories) {
            result.add(category.getName());
        }
        return result.toArray(new String[0]);
    }

    public void CallbackQuery(CallbackQuery callback) {
        Long chatId = callback.from().id();
        String data = callback.data();
        int messageId = callback.message().messageId();
        TgUser tgUser = tgUserRepository.findByChatId(chatId);

        if (tgUser != null && tgUser.getState().equals("TRIP_DETAIL")) {
            if (data.startsWith("day_plus_") || data.startsWith("day_minus_")) {
                String[] parts = data.split("_");
                Long tripId = Long.parseLong(parts[2]);
                int days = Integer.parseInt(parts[3]);

                if (data.startsWith("day_plus_")) days++;
                else if (days > 1) days--;

                Trip trip = tripRepository.findById(tripId).orElse(null);
                if (trip == null) return;

                String newCaption = getTripCaption(trip, days);
                InlineKeyboardMarkup newMarkup = getTripInlineKeyboard(tripId, days);

                EditMessageCaption editCaption = new EditMessageCaption(chatId, messageId);
                editCaption.caption(newCaption);
                editCaption.replyMarkup(newMarkup);
                bot.execute(editCaption);
            } else if (data.startsWith("order_")) {
                String[] parts = data.split("_");
                Long tripId = Long.parseLong(parts[1]);
                int days = Integer.parseInt(parts[2]);

                Trip trip = tripRepository.findById(tripId).orElse(null);
                if (trip == null) return;
                double totalPrice = trip.getPrice() * days;
                if (tgUser.getBalance() < totalPrice) {
                    SendMessage notEnoughMsg = new SendMessage(chatId,
                            "❌ Kechirasiz, sizning balansingizda yetarli mablag' yo'q.\n" +
                                    "💵 Sizda: " + tgUser.getBalance() + " so'm\n" +
                                    "🧾 Zarur: " + totalPrice + " so'm");
                    notEnoughMsg.replyMarkup(genarateMenuButtons());
                    tgUser.setState("TRIP_QILISH");
                    tgUserRepository.save(tgUser);
                    bot.execute(notEnoughMsg);
                    return;
                }
                tgUser.setBalance(tgUser.getBalance() - totalPrice);
                tgUserRepository.save(tgUser);
                String adminChatId = "6377137532";

                String adminMsg = "\uD83D\uDCDD Yangi to‘lov amalga oshirildi!\n\n" +
                        "👤 Foydalanuvchi: " + tgUser.getUsername() + "  (ID: " + tgUser.getChatId() + ")\n" +
                        "📍 Mamlakat: " + trip.getCountry() + "\n" +
                        "✈️ Trip: " + trip.getTitle() + "\n" +
                        "📆 Kunlar soni: " + days + "\n" +
                        "💵 Umumiy narx: " + totalPrice + " so'm\n" +
                        "📅 Sana: " + LocalDate.now();

                SendMessage adminSendMessage = new SendMessage(adminChatId, adminMsg);
                bot.execute(adminSendMessage);
                Booking booking = new Booking();
                booking.setTrip(trip);
                booking.setUser(tgUser);
                booking.setDays(days);
                booking.setBookingDate(LocalDate.now());
                bookingRepository.save(booking);
                if (tgUser.getRefId() != null) {
                    TgUser refUser = tgUserRepository.findById(tgUser.getRefId()).get();
                    Double bonus = totalPrice * 0.1;
                    refUser.setBalance(refUser.getBalance() + bonus);
                    SendMessage sendMessage = new SendMessage(refUser.getChatId(),
                            "🎉 Sizning taklifingiz orqali do‘stingiz "
                                    + ("@" + tgUser.getUsername()
                                    + " birinchi safarini amalga oshirdi!\n"
                                    + "💸 Sizga 10% bonus sifatida " + bonus + " so‘m hisobingizga qo‘shildi."));
                    String adminMsg2 = "🎁 Taklif uchun mukofot: @" + refUser.getUsername() + " foydalanuvchiga 10% bonus berildi.\n"
                            + "🧑‍🤝‍🧑 Taklif qilingan do‘sti birinchi safarini amalga oshirdi.";

                    SendMessage sendMessage1 = new SendMessage(adminChatId, adminMsg2);
                    bot.execute(sendMessage1);
                    bot.execute(sendMessage);
                    tgUser.setRefId(null);
                } else if (tgUser.getRefIdNext() != null) {
                    TgUser refUser = tgUserRepository.findById(tgUser.getRefIdNext()).get();
                    Double bonus = totalPrice * 0.1;
                    refUser.setBalance(refUser.getBalance() + bonus);
                    SendMessage sendMessage = new SendMessage(refUser.getChatId(),
                            "🎉 Sizning taklifingiz orqali do‘stingiz "
                                    + ("@" + tgUser.getUsername()
                                    + "  safarni amalga oshirdi!\n"
                                    + "💸 Sizga 10% bonus sifatida " + bonus + " so‘m hisobingizga qo‘shildi."));
                    String adminMsg2 = "🎁 Taklif uchun mukofot: @" + refUser.getUsername() + " foydalanuvchiga 10% bonus berildi.\n"
                            + "🧑‍🤝‍🧑 Taklif qilingan do‘sti birinchi safarini amalga oshirdi.";

                    SendMessage sendMessage1 = new SendMessage(adminChatId, adminMsg2);
                    bot.execute(sendMessage1);
                    bot.execute(sendMessage);
                    tgUser.setRefIdNext(null);
                }
                SendMessage confirmMsg = new SendMessage(chatId,
                        "✅ Buyurtma muvaffaqiyatli saqlandi!\n\n" +
                                "📍 " + trip.getCountry() + "\n" +
                                "🗓 Kunlar soni: " + days + "\n" +
                                "💵 Umumiy narx: " + (trip.getPrice() * days) + " so'm");
                bot.execute(confirmMsg);
                List<Trip> relatedTrips = tripRepository.findAllByCountryAndIdNot(trip.getCountry(), trip.getId());

                if (!relatedTrips.isEmpty()) {
                    Random random = new Random();
                    Trip recommendedTrip = relatedTrips.get(random.nextInt(relatedTrips.size()));

                    SendPhoto recommendMsg = new SendPhoto(chatId,
                            recommendedTrip.getPhoto().getContent());

                    recommendMsg.caption("📢 Balki bu safar ham sizga yoqadi:\n\n" +
                            "📌 " + recommendedTrip.getTitle() + "\n" +
                            "📍 " + recommendedTrip.getCountry() + "\n" +
                            "💵 Narx: " + recommendedTrip.getPrice() + " so'm");
                    recommendMsg.replyMarkup(new InlineKeyboardMarkup(
                            new InlineKeyboardButton("Korish").callbackData("korish"),
                            new InlineKeyboardButton("Kerak emas").callbackData("kerak emas")
                    ));

                    tgUser.setAdSelectedTripId(recommendedTrip.getId());
                    tgUser.setState("ORDER");
                    tgUserRepository.save(tgUser);
                    bot.execute(recommendMsg);
                    return;
                }
                SendMessage sendMessage1 = new SendMessage(chatId, "Menu:");
                sendMessage1.replyMarkup(genarateMenuButtons());
                tgUser.setState("TRIP_QILISH");
                tgUserRepository.save(tgUser);
                bot.execute(sendMessage1);
            } else if (data.startsWith("back_to_category")) {
                List<Trip> allByCategory = tripRepository.findAllByCategory(categoryRepository.findById(tgUser.getCategoryId()).get());
                ReplyKeyboardMarkup tripButtons = new ReplyKeyboardMarkup(
                        generateTripMatrix(allByCategory)
                );

                SendMessage sendMessage = new SendMessage(chatId, "📋 Triplar ro‘yxati:");
                sendMessage.replyMarkup(tripButtons);
                bot.execute(sendMessage);
                tgUser.setState("TRIP_LIST");
                tgUserRepository.save(tgUser);
            } else if (data.startsWith("send_friend")) {
                String[] s = data.split("_");
                String tripId = s[2];
                String inviteLink = "https://t.me/" + "pdp_exam_project_bot" + "?start=send_" + tgUser.getId() + "_" + tripId;

                String link = """
                        👥 Do‘stingizni taklif qilish uchun quyidagi havolani ulashing:
                        
                        %s
                        
                        Agar do‘stingiz shu havola orqali kirib, ushbu sayohatni xarid qilsa — siz balansingizga 10%% bonus olasiz! 🎁
                        """.formatted(inviteLink);
                SendMessage sendMessage = new SendMessage(chatId, link);
                bot.execute(sendMessage);
            } else if (data.startsWith("back_menu")) {
                SendMessage sendMessage = new SendMessage(
                        chatId,
                        "Quyidagilardan birini tanlang : "
                );
                sendMessage.replyMarkup(genarateMenuButtons());
                tgUser.setState("TRIP_QILISH");
                tgUserRepository.save(tgUser);
                bot.execute(sendMessage);
            }

        } else if (tgUser != null && tgUser.getState().equals("ORDER")) {
            if (data.startsWith("korish")) {
                List<Trip> tripById = tripRepository.getTripById(tgUser.getAdSelectedTripId());
                SendPhoto sendPhoto = new SendPhoto(chatId, tripById.get(0).getPhoto().getContent());
                int days = 1;
                String caption = getTripCaption(tripById.get(0), days);
                InlineKeyboardMarkup inlineKeyboard = getTripInlineKeyboard(tripById.get(0).getId(), days);
                sendPhoto.caption(caption);
                sendPhoto.replyMarkup(inlineKeyboard);
                tgUser.setState("TRIP_DETAIL");
                tgUserRepository.save(tgUser);
                bot.execute(sendPhoto);
            } else if (data.startsWith("kerak emas")) {
                SendMessage sendMessage = new SendMessage(chatId, "Menu");
                sendMessage.replyMarkup(genarateMenuButtons());
                tgUser.setState("TRIP_QILISH");
                tgUserRepository.save(tgUser);
                bot.execute(sendMessage);
            }
        }


    }

    private String getTripCaption(Trip trip, int days) {
        return "📍 " + trip.getCountry() + "\n" +
                "⭐️ " + trip.getRating() + "\n" +
                "💵 Narxi: " + trip.getPrice() + " so'm\n\n" +
                trip.getDescription() + "\n\n" +
                "🧮 Umumiy kun: " + days + "\n" +
                "💰 Umumiy narx: " + (trip.getPrice() * days) + " so'm";
    }

    private InlineKeyboardMarkup getTripInlineKeyboard(Long tripId, int days) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.addRow(
                new InlineKeyboardButton("➖").callbackData("day_minus_" + tripId + "_" + days),
                new InlineKeyboardButton("" + days).callbackData("noop"),
                new InlineKeyboardButton("➕").callbackData("day_plus_" + tripId + "_" + days)
        );
        markup.addRow(
                new InlineKeyboardButton("🛒 Buyurtma berish").callbackData("order_" + tripId + "_" + days)
        );
        markup.addRow(
                new InlineKeyboardButton("\uD83D\uDD17 Do‘stga ulashish").callbackData("send_friend_" + tripId)
        );
        markup.addRow(
                new InlineKeyboardButton("\uD83D\uDCCB MENU").callbackData("back_menu" + tripId)
        );
        markup.addRow(
                new InlineKeyboardButton("⬅️ Orqaga").callbackData("back_to_category")
        );
        return markup;
    }

    private String[][] generateTripMatrix(List<Trip> tripList) {
        List<List<String>> matrix = new ArrayList<>();
        List<String> row = new ArrayList<>();

        for (Trip trip : tripList) {
            row.add(trip.getTitle());
            if (row.size() == 2) {
                matrix.add(new ArrayList<>(row));
                row.clear();
            }
        }

        if (!row.isEmpty()) {
            matrix.add(new ArrayList<>(row));
        }

        List<String> backRow = new ArrayList<>();
        backRow.add("⬅️ Orqaga");
        matrix.add(backRow);

        String[][] result = new String[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            result[i] = matrix.get(i).toArray(new String[0]);
        }

        return result;
    }

    private Keyboard genarateMenuButtons() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton("Trip qilish "), new KeyboardButton("Mening Triplarim")
        ).addRow(
                new KeyboardButton("Hisobni toldirish")
        ).addRow(
                new KeyboardButton("👥 Do‘stni taklif qilish")
        ).resizeKeyboard(true);
    }

    private TgUser getOrCreateTgUser(Long chatId, String username) {
        TgUser byChatId = tgUserRepository.findByChatId(chatId);
        if (byChatId == null) {
            TgUser newTgUser = new TgUser();
            newTgUser.setChatId(chatId);
            newTgUser.setUsername(username);
            return newTgUser;
        }
        tgUserRepository.save(byChatId);

        return byChatId;
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        bot.execute(message);
    }

    public void addTripForChannel(Trip trip) {
        if (trip.getPhoto() != null && trip.getPhoto().getContent() != null) {
            String caption = String.format("""
                            🌍 Yangi Trip Qo‘shildi!
                            📌 Title: %s
                            📖 Description: %s
                            🗺 Country: %s
                            ⭐ Rating: %.1f
                            💰 Price: $%.2f
                            🔗 Ssilka: https://t.me/pdp_exam_project_bot?start=add_%d
                            """,
                    trip.getTitle(),
                    trip.getDescription(),
                    trip.getCountry(),
                    trip.getRating(),
                    trip.getPrice(),
                    trip.getId());
            byte[] content = trip.getPhoto().getContent();


            SendPhoto sendPhoto = new SendPhoto("@pdp_exam_project", content)
                    .caption(caption);
            bot.execute(sendPhoto);

            System.out.println("Trip rasmi bilan caption yuborildi ✅");

        } else {
            String message = String.format("""
                            🌍 Yangi Trip Qo‘shildi!
                            📌 Title: %s
                            📖 Description: %s
                            🗺 Country: %s
                            ⭐ Rating: %.1f
                            💰 Price: $%.2f
                            """,
                    trip.getTitle(),
                    trip.getDescription(),
                    trip.getCountry(),
                    trip.getRating(),
                    trip.getPrice());

            bot.execute(new SendMessage("@pdp_exam_project", message));
        }
    }

    public String[][] getCategoryMatrix(List<Category> categories) {
        List<List<String>> matrix = new ArrayList<>();
        List<String> row = new ArrayList<>();

        for (Category category : categories) {
            row.add(category.getName());
            if (row.size() == 2) {
                matrix.add(new ArrayList<>(row));
                row.clear();
            }
        }

        if (!row.isEmpty()) {
            matrix.add(new ArrayList<>(row));
        }

        // Oxiriga "⬅️ Back" tugmasini qo‘shamiz
        List<String> backRow = new ArrayList<>();
        backRow.add("BACK"); // yoki "⬅️ Back"
        matrix.add(backRow);

        // String[][] massivga o‘tkazamiz
        String[][] result = new String[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            result[i] = matrix.get(i).toArray(new String[0]);
        }

        return result;
    }

    public void sendText(String chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        bot.execute(sendMessage);
    }

    private byte[] downloadFileAsByteArray(String fileUrl) throws Exception {
        URL url = new URL(fileUrl);
        try (InputStream inputStream = url.openStream()) {
            return inputStream.readAllBytes();
        }
    }

    private void advertisingEveryone(Trip trip) {
        List<TgUser> list = bookingRepository.findUsersByTripCountry(trip.getCountry());
        for (TgUser tgUser : list) {
            SendPhoto recommendMsg = new SendPhoto(tgUser.getChatId(),
                    trip.getPhoto().getContent());

            recommendMsg.caption("📢 Siz bu countryga borgan ekansiz balki bu trip ham sizga yoqar:\n\n" +
                    "📌 " + trip.getTitle() + "\n" +
                    "📍 " + trip.getCountry() + "\n" +
                    "💵 Narx: " + trip.getPrice() + " so'm");
            recommendMsg.replyMarkup(new InlineKeyboardMarkup(
                    new InlineKeyboardButton("Korish").callbackData("korish"),
                    new InlineKeyboardButton("Kerak emas").callbackData("kerak emas")
            ));

            tgUser.setAdSelectedTripId(trip.getId());
            tgUser.setState("ORDER");
            tgUserRepository.save(tgUser);
            bot.execute(recommendMsg);
        }
    }

    private void ensureDeleteList(TgUser tgUser) {
        if (tgUser.getDeletedMessageIds() == null)
            tgUser.setDeletedMessageIds(new ArrayList<>());
    }

    private void saveMessageId(TgUser tgUser, SendResponse response) {
        if (response.isOk()) {
            tgUser.getDeletedMessageIds().add(response.message().messageId());
            tgUserRepository.save(tgUser);
        }
    }

}
